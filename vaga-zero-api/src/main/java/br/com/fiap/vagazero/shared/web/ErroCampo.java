package br.com.fiap.vagazero.shared.web;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErroCampo(
        @Schema(example = "cns") String campo,
        @Schema(example = "size must be between 15 and 15") String mensagem) {
}
