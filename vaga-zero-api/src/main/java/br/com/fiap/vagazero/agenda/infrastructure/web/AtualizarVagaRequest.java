package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.agenda.domain.StatusVaga;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarVagaRequest(
        @NotBlank String especialidade,
        @NotBlank String profissional,
        @NotNull LocalDateTime dataHora,
        @NotNull StatusVaga status) {
}
