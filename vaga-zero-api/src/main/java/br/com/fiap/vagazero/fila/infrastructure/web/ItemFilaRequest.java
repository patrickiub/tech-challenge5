package br.com.fiap.vagazero.fila.infrastructure.web;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemFilaRequest(
        @NotNull @Schema(example = "1", description = "Id do paciente - use 1 (Maria Silva, ja cadastrada)")
        Long pacienteId,
        @NotNull @Schema(example = "Oftalmologia") String especialidade,
        @Min(1) @Max(5) @Schema(example = "3", description = "1 (menor) a 5 (maior prioridade clinica)")
        int prioridadeClinica,
        @Schema(example = "true", description = "Se true, pode ser chamado a qualquer momento pela cascata")
        boolean aceitaChamadoImediato,
        @NotNull @Positive @Schema(example = "15.0") BigDecimal raioMaximoKm) {
}
