package br.com.fiap.vagazero.agenda.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;

public record UnidadeRemovidaResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "UBS Vila Mariana") String nome,
        @Schema(example = "true") boolean removida,
        @Schema(example = "0", description = "Quantas unidades restam cadastradas apos esta remocao")
        int totalRestante) {
}
