package br.com.fiap.vagazero.agenda.domain;

import java.util.List;
import java.util.Optional;

public interface PacienteRepositorio {

    Paciente salvar(Paciente paciente);

    Optional<Paciente> buscarPorId(Long id);

    List<Paciente> listarTodos();

    void excluir(Long id);

    boolean existePorCns(String cns);

    /**
     * Usado pelo reset de demonstracao: preserva o paciente fixo criado pela
     * migration V2 (vinculado ao usuario de login PACIENTE de demonstracao).
     */
    void excluirTodosExceto(String cnsFixo);
}
