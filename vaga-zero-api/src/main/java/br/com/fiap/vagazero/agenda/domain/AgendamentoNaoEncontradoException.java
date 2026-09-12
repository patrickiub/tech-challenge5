package br.com.fiap.vagazero.agenda.domain;

import br.com.fiap.vagazero.shared.excecao.RecursoNaoEncontradoException;

public class AgendamentoNaoEncontradoException extends RecursoNaoEncontradoException {

    public AgendamentoNaoEncontradoException(Long id) {
        super("Agendamento nao encontrado: " + id);
    }
}
