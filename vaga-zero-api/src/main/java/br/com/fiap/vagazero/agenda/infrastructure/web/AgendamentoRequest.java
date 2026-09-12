package br.com.fiap.vagazero.agenda.infrastructure.web;

import jakarta.validation.constraints.NotNull;

public record AgendamentoRequest(@NotNull Long vagaId, @NotNull Long pacienteId) {
}
