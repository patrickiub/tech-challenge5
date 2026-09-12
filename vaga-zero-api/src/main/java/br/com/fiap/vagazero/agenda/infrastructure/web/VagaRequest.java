package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VagaRequest(
        @NotNull Long unidadeId,
        @NotBlank String especialidade,
        @NotBlank String profissional,
        @NotNull LocalDateTime dataHora) {
}
