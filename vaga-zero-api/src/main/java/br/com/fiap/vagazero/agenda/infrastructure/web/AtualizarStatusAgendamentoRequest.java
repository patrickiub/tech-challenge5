package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusAgendamentoRequest(@NotNull StatusAgendamento status, LocalDateTime confirmadoEm) {
}
