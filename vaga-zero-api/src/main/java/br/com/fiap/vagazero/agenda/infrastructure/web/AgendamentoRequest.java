package br.com.fiap.vagazero.agenda.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AgendamentoRequest(
        @NotNull @Schema(example = "1", description = "Id da vaga - crie uma vaga antes de executar este exemplo")
        Long vagaId,
        @NotNull @Schema(example = "1", description = "Id do paciente - use 1 (Maria Silva, ja cadastrada)")
        Long pacienteId) {
}
