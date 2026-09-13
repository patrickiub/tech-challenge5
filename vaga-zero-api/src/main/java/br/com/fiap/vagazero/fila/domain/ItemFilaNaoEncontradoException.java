package br.com.fiap.vagazero.fila.domain;

import br.com.fiap.vagazero.shared.excecao.RecursoNaoEncontradoException;

public class ItemFilaNaoEncontradoException extends RecursoNaoEncontradoException {

    public ItemFilaNaoEncontradoException(Long id) {
        super("Item de fila nao encontrado: " + id);
    }
}
