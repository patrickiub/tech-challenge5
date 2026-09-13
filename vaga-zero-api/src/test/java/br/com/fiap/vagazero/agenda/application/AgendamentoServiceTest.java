package br.com.fiap.vagazero.agenda.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.fiap.vagazero.agenda.domain.Agendamento;
import br.com.fiap.vagazero.agenda.domain.AgendamentoInvalidoParaCancelamentoException;
import br.com.fiap.vagazero.agenda.domain.AgendamentoRepositorio;
import br.com.fiap.vagazero.agenda.domain.PacienteRepositorio;
import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;
import br.com.fiap.vagazero.agenda.domain.StatusVaga;
import br.com.fiap.vagazero.agenda.domain.Vaga;
import br.com.fiap.vagazero.agenda.domain.VagaRepositorio;
import br.com.fiap.vagazero.shared.evento.AgendamentoCanceladoEvento;
import br.com.fiap.vagazero.shared.evento.VagaLiberadaEvento;
import br.com.fiap.vagazero.shared.kafka.EventoPublisher;
import br.com.fiap.vagazero.shared.kafka.KafkaTopics;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepositorio agendamentoRepositorio;
    @Mock
    private VagaRepositorio vagaRepositorio;
    @Mock
    private PacienteRepositorio pacienteRepositorio;
    @Mock
    private EventoPublisher eventoPublisher;

    private AgendamentoService agendamentoService;

    private static final Long AGENDAMENTO_ID = 1L;
    private static final Long VAGA_ID = 10L;
    private static final Long PACIENTE_ID = 100L;

    private AgendamentoService criarServico() {
        return new AgendamentoService(agendamentoRepositorio, vagaRepositorio, pacienteRepositorio, eventoPublisher);
    }

    private Agendamento agendamento(StatusAgendamento status) {
        return new Agendamento(AGENDAMENTO_ID, VAGA_ID, PACIENTE_ID, status, null);
    }

    static Stream<StatusAgendamento> statusCancelaveis() {
        return Stream.of(StatusAgendamento.AGENDADO, StatusAgendamento.CONFIRMADO);
    }

    @ParameterizedTest
    @MethodSource("statusCancelaveis")
    void cancelar_apartirDeStatusValido_publicaEventosDeCancelamentoELiberacao(StatusAgendamento statusInicial) {
        agendamentoService = criarServico();
        when(agendamentoRepositorio.buscarPorId(AGENDAMENTO_ID)).thenReturn(Optional.of(agendamento(statusInicial)));
        when(agendamentoRepositorio.salvar(any(Agendamento.class))).thenAnswer(inv -> inv.getArgument(0));
        Vaga vaga = new Vaga(VAGA_ID, 1L, "Oftalmologia", "Dr. Fulano", LocalDateTime.now(), StatusVaga.DISPONIVEL);
        when(vagaRepositorio.buscarPorId(VAGA_ID)).thenReturn(Optional.of(vaga));

        Agendamento resultado = agendamentoService.cancelar(AGENDAMENTO_ID);

        assertThat(resultado.status()).isEqualTo(StatusAgendamento.CANCELADO);
        verify(eventoPublisher).publicar(
                eq(KafkaTopics.AGENDAMENTO_CANCELADO), any(), any(AgendamentoCanceladoEvento.class));
        verify(eventoPublisher).publicar(eq(KafkaTopics.VAGA_LIBERADA), any(), any(VagaLiberadaEvento.class));
    }

    @ParameterizedTest
    @EnumSource(value = StatusAgendamento.class, names = {"CANCELADO", "REALIZADO", "FALTOU"})
    void cancelar_apartirDeStatusInvalido_lancaExcecaoSemPublicarEventos(StatusAgendamento statusInicial) {
        agendamentoService = criarServico();
        when(agendamentoRepositorio.buscarPorId(AGENDAMENTO_ID)).thenReturn(Optional.of(agendamento(statusInicial)));

        assertThatThrownBy(() -> agendamentoService.cancelar(AGENDAMENTO_ID))
                .isInstanceOf(AgendamentoInvalidoParaCancelamentoException.class);

        verify(agendamentoRepositorio, never()).salvar(any());
        verifyNoInteractions(eventoPublisher);
    }
}
