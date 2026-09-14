package br.com.fiap.vagazero.fila.infrastructure.notificacao;

import java.time.Clock;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import br.com.fiap.vagazero.fila.domain.AvisoPendente;
import br.com.fiap.vagazero.fila.domain.AvisoPendenteRepositorio;
import br.com.fiap.vagazero.fila.domain.StatusAviso;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

/**
 * Chama o notificacao-service para avisar um candidato de convite. Protegido
 * por timeout (config do RestClient), retry e circuit breaker, nesta ordem:
 * @Retry envolve @CircuitBreaker (composicao recomendada do Resilience4j -
 * cada tentativa de retry e individualmente contada pelo circuito, ver
 * resilience4j.retry.retry-aspect-order / circuitbreaker.circuit-breaker-
 * aspect-order no application.yml). O fallbackMethod fica so no @Retry: se
 * ficasse tambem no @CircuitBreaker (camada mais interna), o fallback
 * resolveria a chamada ali mesmo e o @Retry nunca veria uma falha para
 * reagir - nenhuma tentativa extra aconteceria.
 *
 * O fallback nunca deixa o convite se perder: o convite ja esta persistido
 * como ENVIADO antes deste cliente ser chamado (e reagido a
 * convite.enviado, publicado depois do convite salvo) - aqui so garantimos
 * que o AVISO em si nao se perde, enfileirando-o em aviso_pendente para
 * reenvio automatico (ReenvioAvisosScheduler).
 */
@Component
public class NotificacaoClient {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoClient.class);
    private static final String INSTANCIA = "notificacao";

    private final RestClient restClient;
    private final AvisoPendenteRepositorio avisoPendenteRepositorio;
    private final Clock clock;

    public NotificacaoClient(
            RestClient notificacaoRestClient, AvisoPendenteRepositorio avisoPendenteRepositorio, Clock clock) {
        this.restClient = notificacaoRestClient;
        this.avisoPendenteRepositorio = avisoPendenteRepositorio;
        this.clock = clock;
    }

    /**
     * Primeira tentativa de aviso, disparada a partir de convite.enviado.
     */
    @Retry(name = INSTANCIA, fallbackMethod = "enviarFallback")
    @CircuitBreaker(name = INSTANCIA)
    public void enviar(AvisoNotificacao aviso) {
        postar(aviso);
    }

    @SuppressWarnings("unused")
    private void enviarFallback(AvisoNotificacao aviso, Throwable causa) {
        LocalDateTime agora = LocalDateTime.now(clock);
        AvisoPendente pendente = new AvisoPendente(
                null, aviso.conviteId(), aviso.destinatario(), aviso.canal(), aviso.mensagem(),
                StatusAviso.PENDENTE, 1, agora, agora);
        avisoPendenteRepositorio.salvar(pendente);
        log.warn(
                "notificacao_indisponivel convite={} destinatario={} motivo={} - aviso enfileirado para reenvio",
                aviso.conviteId(), aviso.destinatario(), causa.toString());
    }

    /**
     * Reenvio de um aviso que ja esta na fila de pendentes (chamado pelo
     * ReenvioAvisosScheduler). Em caso de sucesso, marca o aviso como
     * ENVIADO; em caso de falha, o fallback so incrementa a tentativa e
     * mantem PENDENTE.
     */
    @Retry(name = INSTANCIA, fallbackMethod = "reenviarFallback")
    @CircuitBreaker(name = INSTANCIA)
    public void reenviar(AvisoPendente pendente) {
        AvisoNotificacao aviso = new AvisoNotificacao(
                pendente.destinatario(), pendente.canal(), pendente.mensagem(), pendente.conviteId());
        postar(aviso);

        LocalDateTime agora = LocalDateTime.now(clock);
        AvisoPendente enviado = new AvisoPendente(
                pendente.id(), pendente.conviteId(), pendente.destinatario(), pendente.canal(),
                pendente.mensagem(), StatusAviso.ENVIADO, pendente.tentativas() + 1, pendente.criadoEm(), agora);
        avisoPendenteRepositorio.salvar(enviado);
        log.info("notificacao_reenviada_com_sucesso convite={} tentativas={}", pendente.conviteId(), enviado.tentativas());
    }

    @SuppressWarnings("unused")
    private void reenviarFallback(AvisoPendente pendente, Throwable causa) {
        LocalDateTime agora = LocalDateTime.now(clock);
        AvisoPendente atualizado = new AvisoPendente(
                pendente.id(), pendente.conviteId(), pendente.destinatario(), pendente.canal(),
                pendente.mensagem(), StatusAviso.PENDENTE, pendente.tentativas() + 1, pendente.criadoEm(), agora);
        avisoPendenteRepositorio.salvar(atualizado);
        log.warn(
                "reenvio_notificacao_falhou convite={} tentativas={} motivo={}",
                pendente.conviteId(), atualizado.tentativas(), causa.toString());
    }

    private void postar(AvisoNotificacao aviso) {
        restClient.post()
                .uri("/notificacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .body(aviso)
                .retrieve()
                .toBodilessEntity();
    }
}
