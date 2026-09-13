package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UnidadeRequest(
        @NotBlank @Schema(example = "UBS Vila Mariana") String nome,
        @NotNull @Schema(example = "-23.589000") BigDecimal latitude,
        @NotNull @Schema(example = "-46.642000") BigDecimal longitude) {
}
