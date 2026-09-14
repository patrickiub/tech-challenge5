package br.com.fiap.vagazero.agenda.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.vagazero.agenda.domain.Agendamento;
import br.com.fiap.vagazero.agenda.domain.AgendamentoInvalidoParaCancelamentoException;
import br.com.fiap.vagazero.agenda.domain.AgendamentoInvalidoParaConfirmacaoException;
import br.com.fiap.vagazero.agenda.domain.AgendamentoNaoEncontradoException;
import br.com.fiap.vagazero.agenda.domain.AgendamentoRepositorio;
import br.com.fiap.vagazero.agenda.domain.PacienteNaoEncontradoException;
import br.com.fiap.vagazero.agenda.domain.PacienteRepositorio;
import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;
import br.com.fiap.vagazero.agenda.domain.Vaga;
import br.com.fiap.vagazero.agenda.domain.VagaNaoEncontradaException;
import br.com.fiap.vagazero.agenda.domain.VagaRepositorio;
import br.com.fiap.vagazero.shared.evento.AgendamentoCanceladoEvento;
import br.com.fiap.vagazero.shared.evento.VagaLiberadaEvento;
import br.com.fiap.vagazero.shared.kafka.EventoPublisher;
import br.com.fiap.vagazero.shared.kafka.KafkaTopics;

@Service
public class AgendamentoService implements AgendamentoCascataUseCase, ConsultaAgendamentoUseCase {

    private final AgendamentoRepositorio agendamentoRepositorio;
    private final VagaRepositorio vagaRepositorio;
    private final PacienteRepositorio pacienteRepositorio;
    private final EventoPublisher eventoPublisher;
    private final Clock clock;

    public AgendamentoService(
            AgendamentoRepositorio agendamentoRepositorio,
            VagaRepositorio vagaRepositorio,
            PacienteRepositorio pacienteRepositorio,
            EventoPublisher eventoPublisher,
            Clock clock) {
        this.agendamentoRepositorio = agendamentoRepositorio;
        this.vagaRepositorio = vagaRepositorio;
        this.pacienteRepositorio = pacienteRepositorio;
        this.eventoPublisher = eventoPublisher;
        this.clock = clock;
    }

    public Agendamento criar(Long vagaId, Long pacienteId) {
        vagaRepositorio.buscarPorId(vagaId).orElseThrow(() -> new VagaNaoEncontradaException(vagaId));
        pacienteRepositorio.buscarPorId(pacienteId)
                .orElseThrow(() -> new PacienteNaoEncontradoException(pacienteId));
        Agendamento agendamento = new Agendamento(
                null, vagaId, pacienteId, StatusAgendamento.AGENDADO, null, LocalDateTime.now(clock));
        return agendamentoRepositorio.salvar(agendamento);
    }

    @Override
    public Agendamento buscarPorId(Long id) {
        return agendamentoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new AgendamentoNaoEncontradoException(id));
    }

    public List<Agendamento> listarTodos() {
        return agendamentoRepositorio.listarTodos();
    }

    @Override
    public List<Agendamento> listarPorPaciente(Long pacienteId) {
        return agendamentoRepositorio.listarPorPaciente(pacienteId);
    }

    @Override
    public List<Agendamento> listarPorStatus(StatusAgendamento status) {
        return agendamentoRepositorio.listarPorStatus(status);
    }

    public Agendamento atualizarStatus(Long id, StatusAgendamento status, LocalDateTime confirmadoEm) {
        Agendamento existente = buscarPorId(id);
        Agendamento atualizado = new Agendamento(
                id, existente.vagaId(), existente.pacienteId(), status, confirmadoEm, existente.criadoEm());
        return agendamentoRepositorio.salvar(atualizado);
    }

    /**
     * Confirmacao ativa de presenca pelo paciente: reduz o risco de falta
     * (regra de -40 pontos no motor de scoring) e muda o status para
     * CONFIRMADO.
     */
    public Agendamento confirmarPresenca(Long id) {
        Agendamento existente = buscarPorId(id);
        if (existente.status() != StatusAgendamento.AGENDADO) {
            throw new AgendamentoInvalidoParaConfirmacaoException(id, existente.status());
        }
        Agendamento confirmado = new Agendamento(
                id, existente.vagaId(), existente.pacienteId(), StatusAgendamento.CONFIRMADO,
                LocalDateTime.now(clock), existente.criadoEm());
        return agendamentoRepositorio.salvar(confirmado);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        agendamentoRepositorio.excluir(id);
    }

    /**
     * Cancela o agendamento e libera a vaga para a cascata de convites:
     * publica agendamento.cancelado e, em seguida, vaga.liberada.
     */
    @Override
    public Agendamento cancelar(Long id) {
        Agendamento existente = buscarPorId(id);
        if (existente.status() != StatusAgendamento.AGENDADO && existente.status() != StatusAgendamento.CONFIRMADO) {
            throw new AgendamentoInvalidoParaCancelamentoException(id, existente.status());
        }
        Agendamento cancelado = new Agendamento(
                id, existente.vagaId(), existente.pacienteId(), StatusAgendamento.CANCELADO,
                existente.confirmadoEm(), existente.criadoEm());
        Agendamento salvo = agendamentoRepositorio.salvar(cancelado);

        Vaga vaga = vagaRepositorio.buscarPorId(salvo.vagaId())
                .orElseThrow(() -> new VagaNaoEncontradaException(salvo.vagaId()));

        eventoPublisher.publicar(
                KafkaTopics.AGENDAMENTO_CANCELADO, String.valueOf(salvo.vagaId()),
                new AgendamentoCanceladoEvento(salvo.id(), salvo.vagaId(), salvo.pacienteId()));
        eventoPublisher.publicar(
                KafkaTopics.VAGA_LIBERADA, String.valueOf(vaga.id()),
                new VagaLiberadaEvento(vaga.id(), vaga.especialidade(), vaga.unidadeId(), vaga.dataHora()));

        return salvo;
    }

    @Override
    public Agendamento criarConfirmado(Long vagaId, Long pacienteId, LocalDateTime confirmadoEm) {
        Agendamento agendamento = new Agendamento(
                null, vagaId, pacienteId, StatusAgendamento.CONFIRMADO, confirmadoEm, confirmadoEm);
        return agendamentoRepositorio.salvar(agendamento);
    }
}
