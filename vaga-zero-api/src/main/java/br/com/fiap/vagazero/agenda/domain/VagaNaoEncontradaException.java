package br.com.fiap.vagazero.agenda.domain;

import br.com.fiap.vagazero.shared.excecao.RecursoNaoEncontradoException;

public class VagaNaoEncontradaException extends RecursoNaoEncontradoException {

    public VagaNaoEncontradaException(Long id) {
        super("Vaga nao encontrada: " + id);
    }
}
