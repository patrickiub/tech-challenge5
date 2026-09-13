package br.com.fiap.vagazero.fila.infrastructure.web;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.vagazero.agenda.domain.StatusVaga;
import br.com.fiap.vagazero.fila.application.EstadoCascata;
import io.swagger.v3.oas.annotations.media.Schema;

public record CascataEstadoResponse(
        @Schema(example = "1") Long vagaId,
        @Schema(example = "Oftalmologia") String especialidade,
        @Schema(example = "1") Long unidadeId,
        @Schema(example = "UBS Vila Mariana") String unidadeNome,
        @Schema(example = "EM_CASCATA") StatusVaga statusVaga,
        @Schema(example = "2026-10-15T09:00:00") LocalDateTime dataHoraVaga,
        @Schema(example = "1", description = "Id do convite atualmente ENVIADO, pronto para aceitar/recusar "
                + "em POST /convites/{id}/aceitar ou /recusar. Nulo se nao houver convite ativo no momento.")
        Long conviteAtivoId,
        List<CandidatoCascataResponse> candidatos) {

    public static CascataEstadoResponse de(EstadoCascata estado, Clock clock) {
        List<CandidatoCascataResponse> candidatos = estado.candidatos().stream()
                .map(candidato -> CandidatoCascataResponse.de(candidato, clock))
                .toList();
        Long conviteAtivoId = candidatos.stream()
                .filter(candidato -> "ENVIADO".equals(candidato.statusConvite()))
                .map(CandidatoCascataResponse::conviteId)
                .findFirst()
                .orElse(null);
        return new CascataEstadoResponse(
                estado.vaga().id(), estado.vaga().especialidade(), estado.vaga().unidadeId(),
                estado.unidadeNome(), estado.vaga().status(), estado.vaga().dataHora(), conviteAtivoId,
                candidatos);
    }
}
