package br.com.fiap.vagazero.agenda.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.vagazero.agenda.domain.Agendamento;
import br.com.fiap.vagazero.agenda.domain.AgendamentoInvalidoParaCancelamentoException;
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
public class AgendamentoService implements AgendamentoCascataUseCase {

    private final AgendamentoRepositorio agendamentoRepositorio;
    private final VagaRepositorio vagaRepositorio;
    private final PacienteRepositorio pacienteRepositorio;
    private final EventoPublisher eventoPublisher;

    public AgendamentoService(
            AgendamentoRepositorio agendamentoRepositorio,
            VagaRepositorio vagaRepositorio,
            PacienteRepositorio pacienteRepositorio,
            EventoPublisher eventoPublisher) {
        this.agendamentoRepositorio = agendamentoRepositorio;
        this.vagaRepositorio = vagaRepositorio;
        this.pacienteRepositorio = pacienteRepositorio;
        this.eventoPublisher = eventoPublisher;
    }

    public Agendamento criar(Long vagaId, Long pacienteId) {
        vagaRepositorio.buscarPorId(vagaId).orElseThrow(() -> new VagaNaoEncontradaException(vagaId));
        pacienteRepositorio.buscarPorId(pacienteId)
                .orElseThrow(() -> new PacienteNaoEncontradoException(pacienteId));
        Agendamento agendamento = new Agendamento(null, vagaId, pacienteId, StatusAgendamento.AGENDADO, null);
        return agendamentoRepositorio.salvar(agendamento);
    }

    public Agendamento buscarPorId(Long id) {
        return agendamentoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new AgendamentoNaoEncontradoException(id));
    }

    public List<Agendamento> listarTodos() {
        return agendamentoRepositorio.listarTodos();
    }

    public Agendamento atualizarStatus(Long id, StatusAgendamento status, LocalDateTime confirmadoEm) {
        Agendamento existente = buscarPorId(id);
        Agendamento atualizado = new Agendamento(
                id, existente.vagaId(), existente.pacienteId(), status, confirmadoEm);
        return agendamentoRepositorio.salvar(atualizado);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        agendamentoRepositorio.excluir(id);
    }

    /**
     * Cancela o agendamento e libera a vaga para a cascata de convites:
     * publica agendamento.cancelado e, em seguida, vaga.liberada.
     */
    public Agendamento cancelar(Long id) {
        Agendamento existente = buscarPorId(id);
        if (existente.status() != StatusAgendamento.AGENDADO && existente.status() != StatusAgendamento.CONFIRMADO) {
            throw new AgendamentoInvalidoParaCancelamentoException(id, existente.status());
        }
        Agendamento cancelado = new Agendamento(
                id, existente.vagaId(), existente.pacienteId(), StatusAgendamento.CANCELADO,
                existente.confirmadoEm());
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
                null, vagaId, pacienteId, StatusAgendamento.CONFIRMADO, confirmadoEm);
        return agendamentoRepositorio.salvar(agendamento);
    }
}
