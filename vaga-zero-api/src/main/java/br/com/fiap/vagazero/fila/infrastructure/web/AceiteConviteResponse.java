package br.com.fiap.vagazero.fila.infrastructure.web;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record AceiteConviteResponse(
        @Schema(example = "1") Long conviteId,
        @Schema(example = "1") Long vagaId,
        @Schema(example = "2") Long agendamentoId,
        @Schema(example = "1") Long pacienteId,
        @Schema(example = "Maria da Silva") String nomePaciente,
        @Schema(example = "[\"convite.aceito\", \"vaga.preenchida\"]",
                description = "Eventos Kafka publicados por esta operacao, na ordem em que foram emitidos")
        List<String> eventosPublicados,
        @Schema(description = "Estado da cascata apos o aceite: vaga OCUPADA e sem mais candidatos ativos")
        CascataEstadoResponse estadoAtual) {
}
