package br.com.fiap.vagazero.identidade.domain;

import br.com.fiap.vagazero.shared.excecao.ConflitoException;

public class EmailJaCadastradoException extends ConflitoException {

    public EmailJaCadastradoException(String email) {
        super("Ja existe um usuario cadastrado com o email " + email);
    }
}
