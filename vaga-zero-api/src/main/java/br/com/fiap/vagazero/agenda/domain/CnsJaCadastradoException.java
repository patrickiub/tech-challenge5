package br.com.fiap.vagazero.agenda.domain;

import br.com.fiap.vagazero.shared.excecao.ConflitoException;

public class CnsJaCadastradoException extends ConflitoException {

    public CnsJaCadastradoException(String cns) {
        super("Ja existe um paciente cadastrado com o CNS " + cns);
    }
}
