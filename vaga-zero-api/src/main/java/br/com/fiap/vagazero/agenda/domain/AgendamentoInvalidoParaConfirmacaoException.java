package br.com.fiap.vagazero.agenda.domain;

import br.com.fiap.vagazero.shared.excecao.ConflitoException;

public class AgendamentoInvalidoParaConfirmacaoException extends ConflitoException {

    public AgendamentoInvalidoParaConfirmacaoException(Long id, StatusAgendamento statusAtual) {
        super("Agendamento " + id + " nao pode ter presenca confirmada no status " + statusAtual);
    }
}
