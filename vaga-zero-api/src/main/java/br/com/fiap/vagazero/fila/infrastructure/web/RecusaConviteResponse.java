package br.com.fiap.vagazero.fila.infrastructure.web;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecusaConviteResponse(
        @Schema(example = "1") Long conviteId,
        @Schema(example = "1") Long vagaId,
        @Schema(example = "1") Long pacienteId,
        @Schema(example = "Maria da Silva") String nomePaciente,
        @Schema(example = "[\"convite.expirado\"]",
                description = "Eventos Kafka publicados por esta operacao")
        List<String> eventosPublicados,
        @Schema(description = "Estado da cascata apos a recusa: mostra o proximo candidato convidado, ou "
                + "PERDIDA se a lista se esgotou")
        CascataEstadoResponse estadoAtual) {
}
