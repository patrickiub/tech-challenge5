package br.com.fiap.vagazero.fila.infrastructure.notificacao;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.fiap.vagazero.fila.domain.AvisoPendenteRepositorio;

/**
 * Reprocessamento automatico: quando o notificacao-service volta (ou o
 * circuito fecha), os avisos pendentes sao reenviados sem intervencao
 * manual.
 */
@Component
public class ReenvioAvisosScheduler {

    private final AvisoPendenteRepositorio avisoPendenteRepositorio;
    private final NotificacaoClient notificacaoClient;

    public ReenvioAvisosScheduler(
            AvisoPendenteRepositorio avisoPendenteRepositorio, NotificacaoClient notificacaoClient) {
        this.avisoPendenteRepositorio = avisoPendenteRepositorio;
        this.notificacaoClient = notificacaoClient;
    }

    @Scheduled(fixedDelayString = "${vagazero.notificacao.reenvio-intervalo-ms}")
    public void reprocessarPendentes() {
        avisoPendenteRepositorio.listarPendentes().forEach(notificacaoClient::reenviar);
    }
}
