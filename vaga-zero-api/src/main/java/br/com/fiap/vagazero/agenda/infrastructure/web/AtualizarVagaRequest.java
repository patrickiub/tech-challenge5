package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.agenda.domain.StatusVaga;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarVagaRequest(
        @NotBlank @Schema(example = "Oftalmologia") String especialidade,
        @NotBlank @Schema(example = "Dra. Beatriz Lima") String profissional,
        @NotNull @Schema(example = "2026-10-15T09:00:00") LocalDateTime dataHora,
        @NotNull @Schema(example = "DISPONIVEL") StatusVaga status) {
}
