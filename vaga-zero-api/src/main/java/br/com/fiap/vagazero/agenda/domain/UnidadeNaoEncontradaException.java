package br.com.fiap.vagazero.agenda.domain;

import br.com.fiap.vagazero.shared.excecao.RecursoNaoEncontradoException;

public class UnidadeNaoEncontradaException extends RecursoNaoEncontradoException {

    public UnidadeNaoEncontradaException(Long id) {
        super("Unidade nao encontrada: " + id);
    }
}
