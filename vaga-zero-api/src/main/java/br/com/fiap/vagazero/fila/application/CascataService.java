package br.com.fiap.vagazero.fila.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.fiap.vagazero.agenda.application.AgendamentoCascataUseCase;
import br.com.fiap.vagazero.agenda.application.ConsultaPacienteUseCase;
import br.com.fiap.vagazero.agenda.application.ConsultaUnidadeUseCase;
import br.com.fiap.vagazero.agenda.application.PacienteResumo;
import br.com.fiap.vagazero.agenda.application.UnidadeResumo;
import br.com.fiap.vagazero.agenda.application.VagaCascataUseCase;
import br.com.fiap.vagazero.agenda.domain.Agendamento;
import br.com.fiap.vagazero.agenda.domain.StatusVaga;
import br.com.fiap.vagazero.agenda.domain.Vaga;
import br.com.fiap.vagazero.fila.domain.CandidatoElegivel;
import br.com.fiap.vagazero.fila.domain.Convite;
import br.com.fiap.vagazero.fila.domain.ConviteIndisponivelException;
import br.com.fiap.vagazero.fila.domain.ConviteNaoEncontradoException;
import br.com.fiap.vagazero.fila.domain.ConviteRepositorio;
import br.com.fiap.vagazero.fila.domain.ItemFila;
import br.com.fiap.vagazero.fila.domain.ItemFilaRepositorio;
import br.com.fiap.vagazero.fila.domain.LocalizacaoPaciente;
import br.com.fiap.vagazero.fila.domain.ServicoElegibilidadeCascata;
import br.com.fiap.vagazero.fila.domain.StatusConvite;
import br.com.fiap.vagazero.shared.evento.ConviteAceitoEvento;
import br.com.fiap.vagazero.shared.evento.ConviteEnviadoEvento;
import br.com.fiap.vagazero.shared.evento.ConviteExpiradoEvento;
import br.com.fiap.vagazero.shared.evento.VagaLiberadaEvento;
import br.com.fiap.vagazero.shared.evento.VagaPreenchidaEvento;
import br.com.fiap.vagazero.shared.kafka.EventoPublisher;
import br.com.fiap.vagazero.shared.kafka.KafkaTopics;

/**
 * Motor da cascata de convites. A lista de candidatos elegiveis nunca e
 * persistida: e recalculada a cada progressao a partir da fila e do estado
 * atual dos convites da vaga, pulando quem ja foi convidado. Isso mantem o
 * endpoint de consulta e o motor sempre coerentes com a mesma regra de
 * elegibilidade/ordenacao, sem tabela extra.
 */
@Service
public class CascataService {

    private final ItemFilaRepositorio itemFilaRepositorio;
    private final ConviteRepositorio conviteRepositorio;
    private final ServicoElegibilidadeCascata servicoElegibilidade = new ServicoElegibilidadeCascata();
    private final VagaCascataUseCase vagaCascataUseCase;
    private final AgendamentoCascataUseCase agendamentoCascataUseCase;
    private final ConsultaPacienteUseCase consultaPacienteUseCase;
    private final ConsultaUnidadeUseCase consultaUnidadeUseCase;
    private final EventoPublisher eventoPublisher;
    private final Clock clock;
    private final long ttlSegundos;

    public CascataService(
            ItemFilaRepositorio itemFilaRepositorio,
            ConviteRepositorio conviteRepositorio,
            VagaCascataUseCase vagaCascataUseCase,
            AgendamentoCascataUseCase agendamentoCascataUseCase,
            ConsultaPacienteUseCase consultaPacienteUseCase,
            ConsultaUnidadeUseCase consultaUnidadeUseCase,
            EventoPublisher eventoPublisher,
            Clock clock,
            @Value("${vagazero.convite.ttl-segundos}") long ttlSegundos) {
        this.itemFilaRepositorio = itemFilaRepositorio;
        this.conviteRepositorio = conviteRepositorio;
        this.vagaCascataUseCase = vagaCascataUseCase;
        this.agendamentoCascataUseCase = agendamentoCascataUseCase;
        this.consultaPacienteUseCase = consultaPacienteUseCase;
        this.consultaUnidadeUseCase = consultaUnidadeUseCase;
        this.eventoPublisher = eventoPublisher;
        this.clock = clock;
        this.ttlSegundos = ttlSegundos;
    }

    /**
     * Transiciona a vaga DISPONIVEL -> EM_CASCATA de forma atomica (UPDATE
     * condicional). Se nenhuma linha for afetada, a cascata dessa vaga ja foi
     * iniciada por um processamento anterior - descarta silenciosamente. Kafka
     * e at-least-once, entao vaga.liberada duplicado e esperado (nao bug); essa
     * transicao atomica e o que garante idempotencia do listener sem controle
     * extra de mensagens ja vistas.
     */
    public void iniciarCascata(VagaLiberadaEvento evento) {
        boolean iniciou = vagaCascataUseCase.mudarStatusSeAtual(
                evento.vagaId(), StatusVaga.DISPONIVEL, StatusVaga.EM_CASCATA);
        if (!iniciou) {
            return;
        }
        convidarProximo(evento.vagaId());
    }

    public ResultadoAceite aceitar(Long conviteId) {
        Convite convite = conviteRepositorio.buscarPorId(conviteId)
                .orElseThrow(() -> new ConviteNaoEncontradoException(conviteId));

        boolean conviteAceito = conviteRepositorio.atualizarStatusSeAtual(
                conviteId, StatusConvite.ENVIADO, StatusConvite.ACEITO);
        if (!conviteAceito) {
            throw new ConviteIndisponivelException(conviteId);
        }

        boolean vagaOcupada = vagaCascataUseCase.mudarStatusSeAtual(
                convite.vagaId(), StatusVaga.EM_CASCATA, StatusVaga.OCUPADA);
        if (!vagaOcupada) {
            // Vaga e convite sao linhas diferentes - o UPDATE atomico do convite nao
            // protege a vaga. Reverte o convite para nao deixar um ACEITO orfao.
            conviteRepositorio.atualizarStatusSeAtual(conviteId, StatusConvite.ACEITO, StatusConvite.ENVIADO);
            throw new ConviteIndisponivelException(conviteId);
        }

        Vaga vaga = vagaCascataUseCase.buscarPorId(convite.vagaId());
        LocalDateTime agora = LocalDateTime.now(clock);
        Agendamento agendamento = agendamentoCascataUseCase.criarConfirmado(
                convite.vagaId(), convite.pacienteId(), agora);

        itemFilaRepositorio.buscarPorPacienteEEspecialidade(convite.pacienteId(), vaga.especialidade())
                .ifPresent(item -> itemFilaRepositorio.excluir(item.id()));

        eventoPublisher.publicar(
                KafkaTopics.CONVITE_ACEITO, String.valueOf(convite.vagaId()),
                new ConviteAceitoEvento(convite.id(), convite.vagaId(), convite.pacienteId()));
        eventoPublisher.publicar(
                KafkaTopics.VAGA_PREENCHIDA, String.valueOf(convite.vagaId()),
                new VagaPreenchidaEvento(convite.vagaId(), agendamento.id(), convite.pacienteId()));

        return new ResultadoAceite(convite.vagaId(), agendamento.id(), convite.pacienteId());
    }

    public ResultadoRecusa recusar(Long conviteId) {
        Convite convite = conviteRepositorio.buscarPorId(conviteId)
                .orElseThrow(() -> new ConviteNaoEncontradoException(conviteId));

        boolean recusado = conviteRepositorio.atualizarStatusSeAtual(
                conviteId, StatusConvite.ENVIADO, StatusConvite.RECUSADO);
        if (!recusado) {
            throw new ConviteIndisponivelException(conviteId);
        }

        eventoPublisher.publicar(
                KafkaTopics.CONVITE_EXPIRADO, String.valueOf(convite.vagaId()),
                new ConviteExpiradoEvento(convite.id(), convite.vagaId(), convite.pacienteId(), "RECUSADO"));
        convidarProximo(convite.vagaId());

        return new ResultadoRecusa(convite.id(), convite.vagaId(), convite.pacienteId());
    }

    /**
     * Chamado periodicamente pelo scheduler. Usa a mesma primitiva atomica do
     * aceite, entao um convite que esteja sendo aceito no exato instante da
     * varredura simplesmente nao e afetado por aqui (0 linhas), sem duplicar
     * a progressao da cascata.
     */
    public void expirarVencidos() {
        LocalDateTime agora = LocalDateTime.now(clock);
        List<Convite> vencidos = conviteRepositorio.listarEnviadosExpirados(agora);
        for (Convite convite : vencidos) {
            boolean expirou = conviteRepositorio.atualizarStatusSeAtual(
                    convite.id(), StatusConvite.ENVIADO, StatusConvite.EXPIRADO);
            if (expirou) {
                eventoPublisher.publicar(
                        KafkaTopics.CONVITE_EXPIRADO, String.valueOf(convite.vagaId()),
                        new ConviteExpiradoEvento(convite.id(), convite.vagaId(), convite.pacienteId(), "TTL_EXPIRADO"));
                convidarProximo(convite.vagaId());
            }
        }
    }

    public EstadoCascata consultarEstado(Long vagaId) {
        Vaga vaga = vagaCascataUseCase.buscarPorId(vagaId);
        String unidadeNome = consultaUnidadeUseCase.buscarResumo(vaga.unidadeId()).nome();
        List<CandidatoElegivel> ordenados = planejarCandidatos(vaga);
        Map<Long, Convite> convitePorPaciente = conviteRepositorio.listarPorVaga(vagaId).stream()
                .collect(Collectors.toMap(Convite::pacienteId, c -> c, (a, b) -> a));

        List<CandidatoCascata> linhas = new ArrayList<>();
        int ordem = 1;
        for (CandidatoElegivel candidato : ordenados) {
            Convite convite = convitePorPaciente.get(candidato.itemFila().pacienteId());
            linhas.add(new CandidatoCascata(
                    ordem++, candidato.localizacao().pacienteId(), candidato.localizacao().nome(),
                    candidato.distanciaKm(), convite));
        }
        return new EstadoCascata(vaga, unidadeNome, linhas);
    }

    private void convidarProximo(Long vagaId) {
        Vaga vaga = vagaCascataUseCase.buscarPorId(vagaId);
        if (vaga.status() != StatusVaga.EM_CASCATA) {
            return;
        }

        LocalDateTime agora = LocalDateTime.now(clock);
        if (!vaga.dataHora().isAfter(agora)) {
            vagaCascataUseCase.mudarStatusSeAtual(vagaId, StatusVaga.EM_CASCATA, StatusVaga.PERDIDA);
            return;
        }

        List<CandidatoElegivel> ordenados = planejarCandidatos(vaga);
        Set<Long> jaConvidados = conviteRepositorio.listarPorVaga(vagaId).stream()
                .map(Convite::pacienteId)
                .collect(Collectors.toSet());

        Optional<CandidatoElegivel> proximo = ordenados.stream()
                .filter(candidato -> !jaConvidados.contains(candidato.itemFila().pacienteId()))
                .findFirst();

        if (proximo.isEmpty()) {
            vagaCascataUseCase.mudarStatusSeAtual(vagaId, StatusVaga.EM_CASCATA, StatusVaga.PERDIDA);
            return;
        }

        CandidatoElegivel candidato = proximo.get();
        LocalDateTime expiraEm = agora.plusSeconds(ttlSegundos);
        int ordem = jaConvidados.size() + 1;
        Convite convite = new Convite(
                null, vagaId, candidato.itemFila().pacienteId(), agora, expiraEm, StatusConvite.ENVIADO, ordem);
        Convite salvo = conviteRepositorio.salvar(convite);

        eventoPublisher.publicar(
                KafkaTopics.CONVITE_ENVIADO, String.valueOf(vagaId),
                new ConviteEnviadoEvento(salvo.id(), vagaId, salvo.pacienteId(), expiraEm, ordem));
    }

    private List<CandidatoElegivel> planejarCandidatos(Vaga vaga) {
        List<ItemFila> itens = itemFilaRepositorio.listarPorEspecialidade(vaga.especialidade());
        UnidadeResumo unidade = consultaUnidadeUseCase.buscarResumo(vaga.unidadeId());
        Map<Long, LocalizacaoPaciente> localizacoes = itens.stream()
                .collect(Collectors.toMap(ItemFila::pacienteId, this::localizarPaciente, (a, b) -> a));
        return servicoElegibilidade.selecionarCandidatos(
                itens, vaga.especialidade(), unidade.latitude(), unidade.longitude(), localizacoes);
    }

    private LocalizacaoPaciente localizarPaciente(ItemFila item) {
        PacienteResumo resumo = consultaPacienteUseCase.buscarResumo(item.pacienteId());
        return new LocalizacaoPaciente(resumo.id(), resumo.nome(), resumo.latitude(), resumo.longitude());
    }
}
