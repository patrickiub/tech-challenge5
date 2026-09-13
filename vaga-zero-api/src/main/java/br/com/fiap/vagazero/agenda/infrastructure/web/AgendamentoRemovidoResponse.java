package br.com.fiap.vagazero.agenda.infrastructure.web;

import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;
import io.swagger.v3.oas.annotations.media.Schema;

public record AgendamentoRemovidoResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long pacienteId,
        @Schema(example = "Maria da Silva") String nomePaciente,
        @Schema(example = "1") Long vagaId,
        @Schema(example = "AGENDADO", description = "Status do agendamento no momento em que foi excluido")
        StatusAgendamento statusAnterior,
        @Schema(example = "true") boolean removido,
        @Schema(example = "0", description = "Quantos agendamentos restam cadastrados apos esta remocao")
        int totalRestante) {
}
