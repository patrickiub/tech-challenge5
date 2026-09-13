package br.com.fiap.vagazero.fila.infrastructure.web;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemFilaRequest(
        @NotNull Long pacienteId,
        @NotNull String especialidade,
        @Min(1) @Max(5) int prioridadeClinica,
        boolean aceitaChamadoImediato,
        @NotNull @Positive BigDecimal raioMaximoKm) {
}
