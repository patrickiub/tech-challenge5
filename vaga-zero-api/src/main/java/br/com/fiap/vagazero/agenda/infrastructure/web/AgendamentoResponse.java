package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.agenda.domain.Agendamento;
import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;
import io.swagger.v3.oas.annotations.media.Schema;

public record AgendamentoResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long vagaId,
        @Schema(example = "1") Long pacienteId,
        @Schema(example = "AGENDADO") StatusAgendamento status,
        @Schema(example = "2026-10-10T08:00:00") LocalDateTime confirmadoEm) {

    public static AgendamentoResponse de(Agendamento agendamento) {
        return new AgendamentoResponse(
                agendamento.id(), agendamento.vagaId(), agendamento.pacienteId(),
                agendamento.status(), agendamento.confirmadoEm());
    }
}
