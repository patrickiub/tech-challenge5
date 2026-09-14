package br.com.fiap.vagazero.notificacao.web;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.notificacao.NotificacaoRecebida;

public record NotificacaoResponse(
        long id, String destinatario, String canal, String mensagem, Long conviteId, LocalDateTime recebidoEm) {

    public static NotificacaoResponse de(NotificacaoRecebida notificacao) {
        return new NotificacaoResponse(
                notificacao.id(), notificacao.destinatario(), notificacao.canal(), notificacao.mensagem(),
                notificacao.conviteId(), notificacao.recebidoEm());
    }
}
