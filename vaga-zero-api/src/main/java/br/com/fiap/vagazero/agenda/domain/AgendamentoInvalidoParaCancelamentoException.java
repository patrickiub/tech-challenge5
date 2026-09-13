package br.com.fiap.vagazero.agenda.domain;

import br.com.fiap.vagazero.shared.excecao.ConflitoException;

public class AgendamentoInvalidoParaCancelamentoException extends ConflitoException {

    public AgendamentoInvalidoParaCancelamentoException(Long id, StatusAgendamento statusAtual) {
        super("Agendamento " + id + " nao pode ser cancelado no status " + statusAtual);
    }
}
