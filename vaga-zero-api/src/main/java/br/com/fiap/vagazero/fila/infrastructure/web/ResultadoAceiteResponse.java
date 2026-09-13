package br.com.fiap.vagazero.fila.infrastructure.web;

import br.com.fiap.vagazero.fila.application.ResultadoAceite;
import io.swagger.v3.oas.annotations.media.Schema;

public record ResultadoAceiteResponse(
        @Schema(example = "1") Long vagaId,
        @Schema(example = "2") Long agendamentoId,
        @Schema(example = "1") Long pacienteId) {

    public static ResultadoAceiteResponse de(ResultadoAceite resultado) {
        return new ResultadoAceiteResponse(resultado.vagaId(), resultado.agendamentoId(), resultado.pacienteId());
    }
}
