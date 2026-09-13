package br.com.fiap.vagazero.identidade.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank @Email @Schema(example = "gestor@vagazero.com") String email,
        @NotBlank @Schema(example = "gestor123") String senha) {
}
