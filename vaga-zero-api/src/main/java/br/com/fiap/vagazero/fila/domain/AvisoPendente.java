package br.com.fiap.vagazero.fila.domain;

import java.time.LocalDateTime;

/**
 * Aviso de notificacao de convite que caiu no fallback do Resilience4j
 * (notificacao-service indisponivel) e aguarda reenvio automatico. O
 * convite em si (ENVIADO/ACEITO/...) nunca depende do sucesso deste aviso -
 * e so o registro do aviso em si que precisa ser reprocessado.
 */
public record AvisoPendente(
        Long id,
        Long conviteId,
        String destinatario,
        String canal,
        String mensagem,
        StatusAviso status,
        int tentativas,
        LocalDateTime criadoEm,
        LocalDateTime ultimaTentativaEm) {
}
