package br.com.fiap.vagazero.notificacao.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.notificacao.ChaosState;
import br.com.fiap.vagazero.notificacao.NotificacaoRecebida;
import br.com.fiap.vagazero.notificacao.NotificacaoRepositorioMemoria;
import jakarta.validation.Valid;

/**
 * Recebe pedidos de notificacao e so registra (log estruturado + lista em
 * memoria) - nao envia SMS/WhatsApp/e-mail de verdade. Com o chaos ligado,
 * demora e falha de proposito (ChaosState), para exercitar timeout, retry,
 * circuit breaker e fallback do lado do vaga-zero-api.
 */
@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoController.class);

    private final NotificacaoRepositorioMemoria repositorio;
    private final ChaosState chaosState;

    public NotificacaoController(NotificacaoRepositorioMemoria repositorio, ChaosState chaosState) {
        this.repositorio = repositorio;
        this.chaosState = chaosState;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public NotificacaoResponse receber(@Valid @RequestBody NotificacaoRequest requisicao) throws InterruptedException {
        if (chaosState.estaLigado()) {
            Thread.sleep(chaosState.delayMs());
            throw new ChaosAtivoException();
        }
        NotificacaoRecebida notificacao = repositorio.registrar(
                requisicao.destinatario(), requisicao.canal(), requisicao.mensagem(), requisicao.conviteId());
        log.info(
                "notificacao_recebida destinatario={} canal={} conviteId={} mensagem=\"{}\"",
                notificacao.destinatario(), notificacao.canal(), notificacao.conviteId(), notificacao.mensagem());
        return NotificacaoResponse.de(notificacao);
    }

    @GetMapping
    public List<NotificacaoResponse> listar() {
        return repositorio.listarTodas().stream().map(NotificacaoResponse::de).toList();
    }
}
