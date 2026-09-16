package br.com.fiap.vagazero.risco.application;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.fiap.vagazero.agenda.application.ConsultaAgendamentoUseCase;
import br.com.fiap.vagazero.agenda.application.ConsultaPacienteUseCase;
import br.com.fiap.vagazero.agenda.application.ConsultaUnidadeUseCase;
import br.com.fiap.vagazero.agenda.application.PacienteResumo;
import br.com.fiap.vagazero.agenda.application.UnidadeResumo;
import br.com.fiap.vagazero.agenda.application.VagaCascataUseCase;
import br.com.fiap.vagazero.agenda.domain.Agendamento;
import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;
import br.com.fiap.vagazero.agenda.domain.Vaga;
import br.com.fiap.vagazero.risco.domain.AvaliacaoRisco;
import br.com.fiap.vagazero.risco.domain.AvaliacaoRiscoRepositorio;
import br.com.fiap.vagazero.risco.domain.ClassificacaoRisco;
import br.com.fiap.vagazero.risco.domain.EntradaScoring;
import br.com.fiap.vagazero.risco.domain.MotorScoringRisco;
import br.com.fiap.vagazero.risco.domain.PesosScoring;
import br.com.fiap.vagazero.risco.domain.ResultadoScoring;
import br.com.fiap.vagazero.shared.evento.RiscoAvaliadoEvento;
import br.com.fiap.vagazero.shared.geo.CalculadoraDistancia;
import br.com.fiap.vagazero.shared.kafka.EventoPublisher;
import br.com.fiap.vagazero.shared.kafka.KafkaTopics;

/**
 * Avalia o risco de falta de um agendamento: apura os dados brutos (faltas
 * recentes, distancia, antecedencia, primeira consulta, idade, confirmacao)
 * a partir dos outros modulos via seus casos de uso publicos, roda o motor
 * de regras e persiste + publica risco.avaliado. Recalcula (e persiste uma
 * nova linha) a cada chamada, para que uma confirmacao de presenca recente
 * ja apareca refletida na proxima consulta.
 */
@Service
public class AvaliacaoRiscoService {

    private final ConsultaAgendamentoUseCase consultaAgendamentoUseCase;
    private final VagaCascataUseCase vagaCascataUseCase;
    private final ConsultaPacienteUseCase consultaPacienteUseCase;
    private final ConsultaUnidadeUseCase consultaUnidadeUseCase;
    private final AvaliacaoRiscoRepositorio avaliacaoRiscoRepositorio;
    private final EventoPublisher eventoPublisher;
    private final Clock clock;
    private final MotorScoringRisco motor;

    public AvaliacaoRiscoService(
            ConsultaAgendamentoUseCase consultaAgendamentoUseCase,
            VagaCascataUseCase vagaCascataUseCase,
            ConsultaPacienteUseCase consultaPacienteUseCase,
            ConsultaUnidadeUseCase consultaUnidadeUseCase,
            AvaliacaoRiscoRepositorio avaliacaoRiscoRepositorio,
            EventoPublisher eventoPublisher,
            Clock clock,
            @Value("${vagazero.scoring.pontos-por-falta}") int pontosPorFalta,
            @Value("${vagazero.scoring.teto-pontos-faltas}") int tetoPontosFaltas,
            @Value("${vagazero.scoring.distancia-limite-km}") double distanciaLimiteKm,
            @Value("${vagazero.scoring.pontos-distancia-acima-limite}") int pontosDistancia,
            @Value("${vagazero.scoring.antecedencia-limite-dias}") int antecedenciaLimiteDias,
            @Value("${vagazero.scoring.pontos-antecedencia-acima-limite}") int pontosAntecedencia,
            @Value("${vagazero.scoring.pontos-primeira-consulta-especialidade}") int pontosPrimeiraConsulta,
            @Value("${vagazero.scoring.idade-minima}") int idadeMinima,
            @Value("${vagazero.scoring.idade-maxima}") int idadeMaxima,
            @Value("${vagazero.scoring.pontos-faixa-etaria}") int pontosFaixaEtaria,
            @Value("${vagazero.scoring.pontos-confirmacao-presenca}") int pontosConfirmacaoPresenca,
            @Value("${vagazero.scoring.limite-baixo-medio}") int limiteBaixoMedio,
            @Value("${vagazero.scoring.limite-medio-alto}") int limiteMedioAlto) {
        this.consultaAgendamentoUseCase = consultaAgendamentoUseCase;
        this.vagaCascataUseCase = vagaCascataUseCase;
        this.consultaPacienteUseCase = consultaPacienteUseCase;
        this.consultaUnidadeUseCase = consultaUnidadeUseCase;
        this.avaliacaoRiscoRepositorio = avaliacaoRiscoRepositorio;
        this.eventoPublisher = eventoPublisher;
        this.clock = clock;
        PesosScoring pesos = new PesosScoring(
                pontosPorFalta, tetoPontosFaltas, distanciaLimiteKm, pontosDistancia, antecedenciaLimiteDias,
                pontosAntecedencia, pontosPrimeiraConsulta, idadeMinima, idadeMaxima, pontosFaixaEtaria,
                pontosConfirmacaoPresenca, limiteBaixoMedio, limiteMedioAlto);
        this.motor = new MotorScoringRisco(pesos);
    }

    public AvaliacaoRisco avaliar(Long agendamentoId) {
        Agendamento agendamento = consultaAgendamentoUseCase.buscarPorId(agendamentoId);
        Vaga vaga = vagaCascataUseCase.buscarPorId(agendamento.vagaId());
        PacienteResumo paciente = consultaPacienteUseCase.buscarResumo(agendamento.pacienteId());
        UnidadeResumo unidade = consultaUnidadeUseCase.buscarResumo(vaga.unidadeId());

        EntradaScoring entrada = apurarEntrada(agendamento, vaga, paciente, unidade);
        ResultadoScoring resultado = motor.avaliar(entrada);

        AvaliacaoRisco avaliacao = new AvaliacaoRisco(
                null, agendamentoId, resultado.score(), resultado.classificacao(), LocalDateTime.now(clock),
                resultado.fatores());
        AvaliacaoRisco salva = avaliacaoRiscoRepositorio.salvar(avaliacao);

        eventoPublisher.publicar(
                KafkaTopics.RISCO_AVALIADO, String.valueOf(agendamentoId),
                new RiscoAvaliadoEvento(salva.id(), agendamentoId, salva.score(), salva.classificacao().name()));

        return salva;
    }

    /**
     * Job D-2/D-1: varre agendamentos AGENDADO cuja vaga esta a ate 2 dias de
     * distancia, avalia (no maximo uma vez por dia por agendamento) e, para
     * quem ja chegou em D-1 (ou antes) com classificacao ALTO e sem
     * confirmacao, libera a vaga preventivamente reusando o fluxo normal de
     * cancelamento (que ja publica agendamento.cancelado e vaga.liberada).
     */
    public void processarAgendamentosFuturos() {
        LocalDate hoje = LocalDate.now(clock);
        List<Agendamento> agendados = consultaAgendamentoUseCase.listarPorStatus(StatusAgendamento.AGENDADO);

        for (Agendamento agendamento : agendados) {
            Vaga vaga = vagaCascataUseCase.buscarPorId(agendamento.vagaId());
            long diasAte = ChronoUnit.DAYS.between(hoje, vaga.dataHora().toLocalDate());
            if (diasAte < 0 || diasAte > 2) {
                continue;
            }

            AvaliacaoRisco ultima = avaliacaoRiscoRepositorio.buscarUltimaPorAgendamento(agendamento.id())
                    .orElse(null);
            boolean avaliadoHoje = ultima != null && !ultima.avaliadoEm().toLocalDate().isBefore(hoje);
            if (!avaliadoHoje) {
                ultima = avaliar(agendamento.id());
            }

            if (diasAte <= 1 && ultima.classificacao() == ClassificacaoRisco.ALTO
                    && agendamento.confirmadoEm() == null) {
                consultaAgendamentoUseCase.cancelar(agendamento.id());
            }
        }
    }

    private EntradaScoring apurarEntrada(
            Agendamento agendamento, Vaga vaga, PacienteResumo paciente, UnidadeResumo unidade) {
        List<Agendamento> historico = consultaAgendamentoUseCase.listarPorPaciente(agendamento.pacienteId());

        int faltas = 0;
        boolean primeiraConsulta = true;
        LocalDateTime limiteFaltas = LocalDateTime.now(clock).minusMonths(12);
        for (Agendamento anterior : historico) {
            if (anterior.id().equals(agendamento.id())) {
                continue;
            }
            Vaga vagaAnterior = vagaCascataUseCase.buscarPorId(anterior.vagaId());
            if (vagaAnterior.especialidade().equalsIgnoreCase(vaga.especialidade())) {
                primeiraConsulta = false;
            }
            if (anterior.status() == StatusAgendamento.FALTOU
                    && vagaAnterior.dataHora().isAfter(limiteFaltas)) {
                faltas++;
            }
        }

        double distanciaKm = CalculadoraDistancia.haversineKm(
                unidade.latitude(), unidade.longitude(), paciente.latitude(), paciente.longitude());

        long diasAntecedencia = ChronoUnit.DAYS.between(agendamento.criadoEm(), vaga.dataHora());
        int idade = Period.between(paciente.dataNascimento(), LocalDate.now(clock)).getYears();
        boolean confirmou = agendamento.confirmadoEm() != null;

        return new EntradaScoring(faltas, distanciaKm, diasAntecedencia, primeiraConsulta, idade, confirmou);
    }
}
