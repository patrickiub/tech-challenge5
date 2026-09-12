package br.com.fiap.vagazero.agenda.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.vagazero.agenda.domain.Agendamento;
import br.com.fiap.vagazero.agenda.domain.AgendamentoNaoEncontradoException;
import br.com.fiap.vagazero.agenda.domain.AgendamentoRepositorio;
import br.com.fiap.vagazero.agenda.domain.PacienteNaoEncontradoException;
import br.com.fiap.vagazero.agenda.domain.PacienteRepositorio;
import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;
import br.com.fiap.vagazero.agenda.domain.VagaNaoEncontradaException;
import br.com.fiap.vagazero.agenda.domain.VagaRepositorio;

@Service
public class AgendamentoService {

    private final AgendamentoRepositorio agendamentoRepositorio;
    private final VagaRepositorio vagaRepositorio;
    private final PacienteRepositorio pacienteRepositorio;

    public AgendamentoService(
            AgendamentoRepositorio agendamentoRepositorio,
            VagaRepositorio vagaRepositorio,
            PacienteRepositorio pacienteRepositorio) {
        this.agendamentoRepositorio = agendamentoRepositorio;
        this.vagaRepositorio = vagaRepositorio;
        this.pacienteRepositorio = pacienteRepositorio;
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
}
