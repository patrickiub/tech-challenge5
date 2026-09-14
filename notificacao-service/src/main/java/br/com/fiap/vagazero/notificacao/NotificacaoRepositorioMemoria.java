package br.com.fiap.vagazero.notificacao;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

/**
 * Armazena as notificacoes recebidas na sessao (em memoria - o servico e
 * minimalista e nao tem banco proprio). Usado para evidenciar no video que a
 * mensagem chegou, via GET /notificacoes.
 */
@Component
public class NotificacaoRepositorioMemoria {

    private final Queue<NotificacaoRecebida> recebidas = new ConcurrentLinkedQueue<>();
    private final AtomicLong proximoId = new AtomicLong(1);
    private final Clock clock;

    public NotificacaoRepositorioMemoria(Clock clock) {
        this.clock = clock;
    }

    public NotificacaoRecebida registrar(String destinatario, String canal, String mensagem, Long conviteId) {
        NotificacaoRecebida notificacao = new NotificacaoRecebida(
                proximoId.getAndIncrement(), destinatario, canal, mensagem, conviteId, LocalDateTime.now(clock));
        recebidas.add(notificacao);
        return notificacao;
    }

    public List<NotificacaoRecebida> listarTodas() {
        return List.copyOf(recebidas);
    }
}
