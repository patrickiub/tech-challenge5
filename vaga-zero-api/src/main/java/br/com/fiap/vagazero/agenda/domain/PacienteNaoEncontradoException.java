package br.com.fiap.vagazero.agenda.domain;

import br.com.fiap.vagazero.shared.excecao.RecursoNaoEncontradoException;

public class PacienteNaoEncontradoException extends RecursoNaoEncontradoException {

    public PacienteNaoEncontradoException(Long id) {
        super("Paciente nao encontrado: " + id);
    }
}
