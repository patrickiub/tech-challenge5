package br.com.fiap.vagazero.fila.domain;

import br.com.fiap.vagazero.shared.excecao.RecursoNaoEncontradoException;

public class ConviteNaoEncontradoException extends RecursoNaoEncontradoException {

    public ConviteNaoEncontradoException(Long id) {
        super("Convite nao encontrado: " + id);
    }
}
