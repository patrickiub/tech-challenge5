package br.com.fiap.vagazero.notificacao.web;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.notificacao.ChaosState;

/**
 * Liga/desliga o modo chaos ao vivo durante a gravacao, para provar
 * visualmente que o circuit breaker do vaga-zero-api abre e o fallback
 * funciona.
 */
@RestController
@RequestMapping("/admin/chaos")
public class AdminChaosController {

    private final ChaosState chaosState;

    public AdminChaosController(ChaosState chaosState) {
        this.chaosState = chaosState;
    }

    @PostMapping("/{estado}")
    public ChaosResponse alternar(@PathVariable String estado) {
        switch (estado) {
            case "on" -> chaosState.ligar();
            case "off" -> chaosState.desligar();
            default -> throw new ChaosRequestInvalidoException(estado);
        }
        return new ChaosResponse(chaosState.estaLigado(), chaosState.delayMs());
    }
}
