package br.com.fiap.vagazero.fila.infrastructure.notificacao;

/**
 * Corpo enviado ao notificacao-service (POST /notificacoes).
 */
public record AvisoNotificacao(String destinatario, String canal, String mensagem, Long conviteId) {
}
