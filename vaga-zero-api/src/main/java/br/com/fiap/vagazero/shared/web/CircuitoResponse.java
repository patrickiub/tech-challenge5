package br.com.fiap.vagazero.shared.web;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.swagger.v3.oas.annotations.media.Schema;

public record CircuitoResponse(
        @Schema(example = "notificacao") String nome,
        @Schema(example = "OPEN", description = "CLOSED, OPEN, HALF_OPEN, DISABLED ou FORCED_OPEN") String estado,
        @Schema(example = "100.0", description = "Percentual de falha na janela atual, -1 se ainda nao ha "
                + "chamadas suficientes para calcular")
        float taxaFalhaPercentual,
        @Schema(example = "4") int chamadasNaJanela,
        @Schema(example = "4") int chamadasComFalha,
        @Schema(example = "0") int chamadasComSucesso,
        @Schema(example = "2") long chamadasNaoPermitidas) {

    public static CircuitoResponse de(String nome, CircuitBreaker circuitBreaker) {
        CircuitBreaker.Metrics metricas = circuitBreaker.getMetrics();
        return new CircuitoResponse(
                nome,
                circuitBreaker.getState().name(),
                metricas.getFailureRate(),
                metricas.getNumberOfBufferedCalls(),
                metricas.getNumberOfFailedCalls(),
                metricas.getNumberOfSuccessfulCalls(),
                metricas.getNumberOfNotPermittedCalls());
    }
}
