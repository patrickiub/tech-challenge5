package br.com.fiap.vagazero.agenda.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;

public record PacienteRemovidoResponse(
        @Schema(example = "2") Long id,
        @Schema(example = "Joao Pereira") String nome,
        @Schema(example = "700000000000012") String cns,
        @Schema(example = "true") boolean removido,
        @Schema(example = "3", description = "Quantos pacientes restam cadastrados apos esta remocao")
        int totalRestante) {
}
