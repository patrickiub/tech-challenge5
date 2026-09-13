package br.com.fiap.vagazero.agenda.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;

public record VagaRemovidaResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Oftalmologia") String especialidade,
        @Schema(example = "UBS Vila Mariana") String unidadeNome,
        @Schema(example = "true") boolean removida,
        @Schema(example = "0", description = "Quantas vagas restam cadastradas apos esta remocao")
        int totalRestante) {
}
