package br.com.fiap.vagazero.identidade.domain;

import br.com.fiap.vagazero.shared.excecao.VagaZeroException;

public class CredenciaisInvalidasException extends VagaZeroException {

    public CredenciaisInvalidasException() {
        super("Email ou senha invalidos");
    }
}
