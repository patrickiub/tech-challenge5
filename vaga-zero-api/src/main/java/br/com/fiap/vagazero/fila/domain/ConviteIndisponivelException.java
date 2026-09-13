package br.com.fiap.vagazero.fila.domain;

import br.com.fiap.vagazero.shared.excecao.ConflitoException;

/**
 * Lancada quando um convite ja nao esta mais em ENVIADO no momento do
 * aceite/recusa (ja tratado por outra requisicao, expirado pelo scheduler,
 * ou a vaga nao pode mais ser ocupada). Sinaliza corrida concorrente, nao
 * bug: quem chamou perdeu a janela.
 */
public class ConviteIndisponivelException extends ConflitoException {

    public ConviteIndisponivelException(Long conviteId) {
        super("Convite " + conviteId + " nao esta mais disponivel");
    }
}
