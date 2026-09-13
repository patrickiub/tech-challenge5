package br.com.fiap.vagazero.fila.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;

public record ItemFilaRemovidoResponse(
        @Schema(example = "1") Long pacienteId,
        @Schema(example = "Maria da Silva") String nomePaciente,
        @Schema(example = "Oftalmologia") String especialidade,
        @Schema(example = "true") boolean removidoDaFila,
        @Schema(example = "0", description = "Quantos pacientes restam na fila desta especialidade")
        int restantesNaEspecialidade) {
}
