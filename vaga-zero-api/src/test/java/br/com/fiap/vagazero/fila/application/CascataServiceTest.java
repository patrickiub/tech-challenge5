package br.com.fiap.vagazero.fila.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.fiap.vagazero.agenda.application.AgendamentoCascataUseCase;
import br.com.fiap.vagazero.agenda.application.ConsultaPacienteUseCase;
import br.com.fiap.vagazero.agenda.application.ConsultaUnidadeUseCase;
import br.com.fiap.vagazero.agenda.application.PacienteResumo;
import br.com.fiap.vagazero.agenda.application.UnidadeResumo;
import br.com.fiap.vagazero.agenda.application.VagaCascataUseCase;
import br.com.fiap.vagazero.agenda.domain.Agendamento;
import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;
import br.com.fiap.vagazero.agenda.domain.StatusVaga;
import br.com.fiap.vagazero.agenda.domain.Vaga;
import br.com.fiap.vagazero.fila.domain.Convite;
import br.com.fiap.vagazero.fila.domain.ConviteIndisponivelException;
import br.com.fiap.vagazero.fila.domain.ConviteRepositorio;
import br.com.fiap.vagazero.fila.domain.ItemFila;
import br.com.fiap.vagazero.fila.domain.ItemFilaRepositorio;
import br.com.fiap.vagazero.fila.domain.StatusConvite;
import br.com.fiap.vagazero.shared.evento.ConviteEnviadoEvento;
import br.com.fiap.vagazero.shared.evento.ConviteExpiradoEvento;
import br.com.fiap.vagazero.shared.evento.VagaLiberadaEvento;
import br.com.fiap.vagazero.shared.kafka.EventoPublisher;
import br.com.fiap.vagazero.shared.kafka.KafkaTopics;

@ExtendWith(MockitoExtension.class)
class CascataServiceTest {

    private static final String ESPECIALIDADE = "Oftalmologia";
    private static final Long VAGA_ID = 10L;
    private static final Long UNIDADE_ID = 1L;

    @Mock
    private ItemFilaRepositorio itemFilaRepositorio;
    @Mock
    private ConviteRepositorio conviteRepositorio;
    @Mock
    private VagaCascataUseCase vagaCascataUseCase;
    @Mock
    private AgendamentoCascataUseCase agendamentoCascataUseCase;
    @Mock
    private ConsultaPacienteUseCase consultaPacienteUseCase;
    @Mock
    private ConsultaUnidadeUseCase consultaUnidadeUseCase;
    @Mock
    private EventoPublisher eventoPublisher;

    private Clock clock;
    private CascataService cascataService;

    @BeforeEach
    void configurar() {
        clock = Clock.fixed(Instant.parse("2026-09-13T10:00:00Z"), ZoneOffset.UTC);
        cascataService = new CascataService(
                itemFilaRepositorio, conviteRepositorio, vagaCascataUseCase, agendamentoCascataUseCase,
                consultaPacienteUseCase, consultaUnidadeUseCase, eventoPublisher, clock, 1800L);
    }

    private void mockarUnidade() {
        when(consultaUnidadeUseCase.buscarResumo(UNIDADE_ID))
                .thenReturn(new UnidadeResumo(UNIDADE_ID, "Unidade Central", BigDecimal.ZERO, BigDecimal.ZERO));
    }

    private Vaga vaga(StatusVaga status) {
        return new Vaga(VAGA_ID, UNIDADE_ID, ESPECIALIDADE, "Dr. Fulano", LocalDateTime.now(clock).plusDays(10), status);
    }

    private ItemFila item(long pacienteId, int prioridade, LocalDateTime dataEntrada) {
        return new ItemFila(pacienteId, pacienteId, ESPECIALIDADE, dataEntrada, prioridade, true, BigDecimal.TEN);
    }

    private void mockarLocalizacao(long pacienteId) {
        when(consultaPacienteUseCase.buscarResumo(pacienteId))
                .thenReturn(new PacienteResumo(pacienteId, "Paciente " + pacienteId, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    @Test
    void iniciarCascata_quandoVagaDisponivel_convidaPrimeiroCandidato() {
        when(vagaCascataUseCase.mudarStatusSeAtual(VAGA_ID, StatusVaga.DISPONIVEL, StatusVaga.EM_CASCATA))
                .thenReturn(true);
        when(vagaCascataUseCase.buscarPorId(VAGA_ID)).thenReturn(vaga(StatusVaga.EM_CASCATA));
        mockarUnidade();
        ItemFila candidato = item(100L, 5, LocalDateTime.now(clock).minusDays(5));
        when(itemFilaRepositorio.listarPorEspecialidade(ESPECIALIDADE)).thenReturn(List.of(candidato));
        mockarLocalizacao(100L);
        when(conviteRepositorio.listarPorVaga(VAGA_ID)).thenReturn(List.of());
        when(conviteRepositorio.salvar(any(Convite.class))).thenAnswer(inv -> {
            Convite c = inv.getArgument(0);
            return new Convite(1L, c.vagaId(), c.pacienteId(), c.enviadoEm(), c.expiraEm(), c.status(), c.ordemNaCascata());
        });

        cascataService.iniciarCascata(new VagaLiberadaEvento(VAGA_ID, ESPECIALIDADE, UNIDADE_ID, LocalDateTime.now(clock).plusDays(10)));

        ArgumentCaptor<Convite> captor = ArgumentCaptor.forClass(Convite.class);
        verify(conviteRepositorio).salvar(captor.capture());
        assertThat(captor.getValue().pacienteId()).isEqualTo(100L);
        assertThat(captor.getValue().status()).isEqualTo(StatusConvite.ENVIADO);
        assertThat(captor.getValue().ordemNaCascata()).isEqualTo(1);
        verify(eventoPublisher).publicar(eq(KafkaTopics.CONVITE_ENVIADO), any(), any(ConviteEnviadoEvento.class));
    }

    @Test
    void iniciarCascata_quandoEventoVagaLiberadaDuplicado_naoIniciaSegundaCascata() {
        // Simula o segundo vaga.liberada chegando: a vaga ja saiu de DISPONIVEL
        // na primeira entrega, entao o UPDATE atomico nao afeta nenhuma linha.
        when(vagaCascataUseCase.mudarStatusSeAtual(VAGA_ID, StatusVaga.DISPONIVEL, StatusVaga.EM_CASCATA))
                .thenReturn(false);

        cascataService.iniciarCascata(new VagaLiberadaEvento(VAGA_ID, ESPECIALIDADE, UNIDADE_ID, LocalDateTime.now(clock).plusDays(10)));

        verify(vagaCascataUseCase, never()).buscarPorId(anyLong());
        verify(conviteRepositorio, never()).salvar(any());
        verifyNoInteractions(eventoPublisher);
    }

    @Test
    void aceitar_feliz_ocupaVagaCriaAgendamentoERemoveDaFila() {
        Convite convite = new Convite(5L, VAGA_ID, 100L, LocalDateTime.now(clock), LocalDateTime.now(clock).plusMinutes(30), StatusConvite.ENVIADO, 1);
        when(conviteRepositorio.buscarPorId(5L)).thenReturn(Optional.of(convite));
        when(conviteRepositorio.atualizarStatusSeAtual(5L, StatusConvite.ENVIADO, StatusConvite.ACEITO)).thenReturn(true);
        when(vagaCascataUseCase.mudarStatusSeAtual(VAGA_ID, StatusVaga.EM_CASCATA, StatusVaga.OCUPADA)).thenReturn(true);
        when(vagaCascataUseCase.buscarPorId(VAGA_ID)).thenReturn(vaga(StatusVaga.OCUPADA));
        Agendamento agendamento = new Agendamento(50L, VAGA_ID, 100L, StatusAgendamento.CONFIRMADO, LocalDateTime.now(clock));
        when(agendamentoCascataUseCase.criarConfirmado(eq(VAGA_ID), eq(100L), any())).thenReturn(agendamento);
        ItemFila itemNaFila = item(100L, 3, LocalDateTime.now(clock).minusDays(1));
        when(itemFilaRepositorio.buscarPorPacienteEEspecialidade(100L, ESPECIALIDADE)).thenReturn(Optional.of(itemNaFila));

        ResultadoAceite resultado = cascataService.aceitar(5L);

        assertThat(resultado.vagaId()).isEqualTo(VAGA_ID);
        assertThat(resultado.agendamentoId()).isEqualTo(50L);
        verify(itemFilaRepositorio).excluir(100L);
        verify(eventoPublisher).publicar(eq(KafkaTopics.CONVITE_ACEITO), any(), any());
        verify(eventoPublisher).publicar(eq(KafkaTopics.VAGA_PREENCHIDA), any(), any());
    }

    @Test
    void aceitar_quandoConviteJaTratado_lancaExcecaoSemEfeitosColaterais() {
        Convite convite = new Convite(5L, VAGA_ID, 100L, LocalDateTime.now(clock), LocalDateTime.now(clock).plusMinutes(30), StatusConvite.ENVIADO, 1);
        when(conviteRepositorio.buscarPorId(5L)).thenReturn(Optional.of(convite));
        when(conviteRepositorio.atualizarStatusSeAtual(5L, StatusConvite.ENVIADO, StatusConvite.ACEITO)).thenReturn(false);

        assertThatThrownBy(() -> cascataService.aceitar(5L)).isInstanceOf(ConviteIndisponivelException.class);

        verify(vagaCascataUseCase, never()).mudarStatusSeAtual(anyLong(), any(), any());
        verifyNoInteractions(agendamentoCascataUseCase);
        verifyNoInteractions(eventoPublisher);
    }

    @Test
    void aceitar_quandoVagaNaoPodeSerOcupada_reverteConviteELanca409() {
        Convite convite = new Convite(5L, VAGA_ID, 100L, LocalDateTime.now(clock), LocalDateTime.now(clock).plusMinutes(30), StatusConvite.ENVIADO, 1);
        when(conviteRepositorio.buscarPorId(5L)).thenReturn(Optional.of(convite));
        when(conviteRepositorio.atualizarStatusSeAtual(5L, StatusConvite.ENVIADO, StatusConvite.ACEITO)).thenReturn(true);
        when(vagaCascataUseCase.mudarStatusSeAtual(VAGA_ID, StatusVaga.EM_CASCATA, StatusVaga.OCUPADA)).thenReturn(false);

        assertThatThrownBy(() -> cascataService.aceitar(5L)).isInstanceOf(ConviteIndisponivelException.class);

        verify(conviteRepositorio).atualizarStatusSeAtual(5L, StatusConvite.ACEITO, StatusConvite.ENVIADO);
        verifyNoInteractions(agendamentoCascataUseCase);
        verify(itemFilaRepositorio, never()).excluir(anyLong());
        verifyNoInteractions(eventoPublisher);
    }

    @Test
    void recusar_quandoHaProximoCandidato_publicaExpiradoEConvidaProximo() {
        Convite conviteRecusado = new Convite(5L, VAGA_ID, 100L, LocalDateTime.now(clock), LocalDateTime.now(clock).plusMinutes(30), StatusConvite.ENVIADO, 1);
        when(conviteRepositorio.buscarPorId(5L)).thenReturn(Optional.of(conviteRecusado));
        when(conviteRepositorio.atualizarStatusSeAtual(5L, StatusConvite.ENVIADO, StatusConvite.RECUSADO)).thenReturn(true);

        when(vagaCascataUseCase.buscarPorId(VAGA_ID)).thenReturn(vaga(StatusVaga.EM_CASCATA));
        mockarUnidade();
        ItemFila primeiro = item(100L, 5, LocalDateTime.now(clock).minusDays(10));
        ItemFila segundo = item(200L, 3, LocalDateTime.now(clock).minusDays(5));
        when(itemFilaRepositorio.listarPorEspecialidade(ESPECIALIDADE)).thenReturn(List.of(primeiro, segundo));
        mockarLocalizacao(100L);
        mockarLocalizacao(200L);
        // O convite recusado ja existe para o paciente 100, entao ele e pulado.
        when(conviteRepositorio.listarPorVaga(VAGA_ID)).thenReturn(List.of(conviteRecusado));
        when(conviteRepositorio.salvar(any(Convite.class))).thenAnswer(inv -> {
            Convite c = inv.getArgument(0);
            return new Convite(6L, c.vagaId(), c.pacienteId(), c.enviadoEm(), c.expiraEm(), c.status(), c.ordemNaCascata());
        });

        cascataService.recusar(5L);

        verify(eventoPublisher).publicar(eq(KafkaTopics.CONVITE_EXPIRADO), any(), any(ConviteExpiradoEvento.class));
        ArgumentCaptor<Convite> captor = ArgumentCaptor.forClass(Convite.class);
        verify(conviteRepositorio).salvar(captor.capture());
        assertThat(captor.getValue().pacienteId()).isEqualTo(200L);
        assertThat(captor.getValue().ordemNaCascata()).isEqualTo(2);
    }

    @Test
    void recusar_quandoListaEstaEsgotada_marcaVagaComoPerdida() {
        Convite conviteRecusado = new Convite(5L, VAGA_ID, 100L, LocalDateTime.now(clock), LocalDateTime.now(clock).plusMinutes(30), StatusConvite.ENVIADO, 1);
        when(conviteRepositorio.buscarPorId(5L)).thenReturn(Optional.of(conviteRecusado));
        when(conviteRepositorio.atualizarStatusSeAtual(5L, StatusConvite.ENVIADO, StatusConvite.RECUSADO)).thenReturn(true);

        when(vagaCascataUseCase.buscarPorId(VAGA_ID)).thenReturn(vaga(StatusVaga.EM_CASCATA));
        mockarUnidade();
        ItemFila unico = item(100L, 5, LocalDateTime.now(clock).minusDays(10));
        when(itemFilaRepositorio.listarPorEspecialidade(ESPECIALIDADE)).thenReturn(List.of(unico));
        mockarLocalizacao(100L);
        when(conviteRepositorio.listarPorVaga(VAGA_ID)).thenReturn(List.of(conviteRecusado));

        cascataService.recusar(5L);

        verify(vagaCascataUseCase).mudarStatusSeAtual(VAGA_ID, StatusVaga.EM_CASCATA, StatusVaga.PERDIDA);
        verify(conviteRepositorio, never()).salvar(any());
    }

    @Test
    void expirarVencidos_expiraEConvidaProximo() {
        Convite vencido = new Convite(5L, VAGA_ID, 100L, LocalDateTime.now(clock).minusHours(1), LocalDateTime.now(clock).minusMinutes(1), StatusConvite.ENVIADO, 1);
        when(conviteRepositorio.listarEnviadosExpirados(any())).thenReturn(List.of(vencido));
        when(conviteRepositorio.atualizarStatusSeAtual(5L, StatusConvite.ENVIADO, StatusConvite.EXPIRADO)).thenReturn(true);
        when(vagaCascataUseCase.buscarPorId(VAGA_ID)).thenReturn(vaga(StatusVaga.EM_CASCATA));
        mockarUnidade();
        when(itemFilaRepositorio.listarPorEspecialidade(ESPECIALIDADE)).thenReturn(List.of());
        when(conviteRepositorio.listarPorVaga(VAGA_ID)).thenReturn(List.of(vencido));

        cascataService.expirarVencidos();

        verify(eventoPublisher).publicar(eq(KafkaTopics.CONVITE_EXPIRADO), any(), any(ConviteExpiradoEvento.class));
        verify(vagaCascataUseCase).mudarStatusSeAtual(VAGA_ID, StatusVaga.EM_CASCATA, StatusVaga.PERDIDA);
    }

    @Test
    void expirarVencidos_quandoConviteFoiAceitoNoMeioTempo_naoDuplicaProgressao() {
        Convite disputado = new Convite(5L, VAGA_ID, 100L, LocalDateTime.now(clock).minusHours(1), LocalDateTime.now(clock).minusMinutes(1), StatusConvite.ENVIADO, 1);
        when(conviteRepositorio.listarEnviadosExpirados(any())).thenReturn(List.of(disputado));
        // Corrida: o aceite ja mudou o status entre a leitura e a tentativa de expirar.
        when(conviteRepositorio.atualizarStatusSeAtual(5L, StatusConvite.ENVIADO, StatusConvite.EXPIRADO)).thenReturn(false);

        cascataService.expirarVencidos();

        verify(vagaCascataUseCase, never()).buscarPorId(anyLong());
        verifyNoInteractions(eventoPublisher);
    }
}
