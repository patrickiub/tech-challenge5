package br.com.fiap.vagazero.agenda.domain;

import java.util.List;
import java.util.Optional;

public interface PacienteRepositorio {

    Paciente salvar(Paciente paciente);

    Optional<Paciente> buscarPorId(Long id);

    List<Paciente> listarTodos();

    void excluir(Long id);

    boolean existePorCns(String cns);
}
