package br.com.fiap.vagazero.agenda.domain;

import java.util.List;
import java.util.Optional;

public interface AgendamentoRepositorio {

    Agendamento salvar(Agendamento agendamento);

    Optional<Agendamento> buscarPorId(Long id);

    List<Agendamento> listarTodos();

    List<Agendamento> listarPorPaciente(Long pacienteId);

    List<Agendamento> listarPorStatus(StatusAgendamento status);

    void excluir(Long id);
}
