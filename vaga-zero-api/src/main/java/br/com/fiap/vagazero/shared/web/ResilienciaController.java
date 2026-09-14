package br.com.fiap.vagazero.shared.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Observabilidade do circuit breaker da chamada a notificacao-service - le
 * direto do registry do Resilience4j, sem passar por nenhum estado proprio.
 * Pensado para ficar legivel na tela durante a gravacao (liga o chaos no
 * notificacao-service, observa o estado mudar aqui).
 */
@Tag(name = "6 - Resiliencia", description = "Estado do circuit breaker e avisos pendentes de reenvio ao "
        + "notificacao-service.")
@RestController
@RequestMapping("/admin")
public class ResilienciaController {

    private static final String INSTANCIA_NOTIFICACAO = "notificacao";

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public ResilienciaController(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @Operation(summary = "Consultar estado do circuit breaker de notificacao",
            description = "Estado atual (CLOSED/OPEN/HALF_OPEN), taxa de falha e numero de chamadas na janela "
                    + "deslizante. Ligue o chaos no notificacao-service (POST :8081/admin/chaos/on) e dispare "
                    + "uma cascata para ver o estado mudar para OPEN em poucos segundos.")
    @GetMapping("/circuito")
    public CircuitoResponse circuito() {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(INSTANCIA_NOTIFICACAO);
        return CircuitoResponse.de(INSTANCIA_NOTIFICACAO, circuitBreaker);
    }
}
