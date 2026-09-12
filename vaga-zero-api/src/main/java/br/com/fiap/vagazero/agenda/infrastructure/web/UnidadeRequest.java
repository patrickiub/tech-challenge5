package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UnidadeRequest(
        @NotBlank String nome,
        @NotNull BigDecimal latitude,
        @NotNull BigDecimal longitude) {
}
