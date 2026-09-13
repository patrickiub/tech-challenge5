package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusAgendamentoRequest(
        @NotNull @Schema(example = "CONFIRMADO") StatusAgendamento status,
        @Schema(example = "2026-10-10T08:00:00") LocalDateTime confirmadoEm) {
}
