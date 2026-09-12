package br.com.fiap.vagazero.identidade.domain;

import br.com.fiap.vagazero.shared.excecao.VagaZeroException;

public class EmailJaCadastradoException extends VagaZeroException {

    public EmailJaCadastradoException(String email) {
        super("Ja existe um usuario cadastrado com o email " + email);
    }
}
