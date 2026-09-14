package br.com.fiap.vagazero.notificacao;

import java.time.LocalDateTime;

public record NotificacaoRecebida(
        long id, String destinatario, String canal, String mensagem, Long conviteId, LocalDateTime recebidoEm) {
}
