package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VagaRequest(
        @NotNull @Schema(example = "1", description = "Id da unidade - crie uma unidade antes de executar este exemplo")
        Long unidadeId,
        @NotBlank @Schema(example = "Oftalmologia") String especialidade,
        @NotBlank @Schema(example = "Dra. Beatriz Lima") String profissional,
        @NotNull @Schema(example = "2026-10-15T09:00:00") LocalDateTime dataHora) {
}
