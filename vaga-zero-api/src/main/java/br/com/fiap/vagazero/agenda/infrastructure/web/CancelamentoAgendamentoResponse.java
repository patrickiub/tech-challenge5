package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.util.List;

import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;
import io.swagger.v3.oas.annotations.media.Schema;

public record CancelamentoAgendamentoResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "CANCELADO") StatusAgendamento status,
        @Schema(example = "1") Long pacienteId,
        @Schema(example = "Maria da Silva") String nomePaciente,
        @Schema(example = "1") Long vagaId,
        @Schema(example = "Oftalmologia") String especialidade,
        @Schema(example = "[\"agendamento.cancelado\", \"vaga.liberada\"]",
                description = "Eventos Kafka publicados por esta operacao, na ordem em que foram emitidos")
        List<String> eventosPublicados) {
}
