package br.com.fiap.vagazero.notificacao;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Estado do modo chaos: quando ligado, POST /notificacoes demora e falha de
 * proposito, para demonstrar ao vivo a abertura do circuito no
 * vaga-zero-api. Alternado via POST /admin/chaos/{on|off}.
 */
@Component
public class ChaosState {

    private final AtomicBoolean ligado = new AtomicBoolean(false);
    private final long delayMs;

    public ChaosState(@Value("${vagazero.chaos.delay-ms:3000}") long delayMs) {
        this.delayMs = delayMs;
    }

    public void ligar() {
        ligado.set(true);
    }

    public void desligar() {
        ligado.set(false);
    }

    public boolean estaLigado() {
        return ligado.get();
    }

    public long delayMs() {
        return delayMs;
    }
}
