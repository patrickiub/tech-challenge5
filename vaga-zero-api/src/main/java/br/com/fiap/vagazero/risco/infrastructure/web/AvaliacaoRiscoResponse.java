package br.com.fiap.vagazero.risco.infrastructure.web;

import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.vagazero.risco.domain.AvaliacaoRisco;
import br.com.fiap.vagazero.risco.domain.ClassificacaoRisco;
import io.swagger.v3.oas.annotations.media.Schema;

public record AvaliacaoRiscoResponse(
        @Schema(example = "1") Long agendamentoId,
        @Schema(example = "65") int score,
        @Schema(example = "ALTO") ClassificacaoRisco classificacao,
        @Schema(example = "2026-09-14T08:00:00") LocalDateTime avaliadoEm,
        List<FatorRiscoResponse> fatores) {

    public static AvaliacaoRiscoResponse de(AvaliacaoRisco avaliacao) {
        return new AvaliacaoRiscoResponse(
                avaliacao.agendamentoId(), avaliacao.score(), avaliacao.classificacao(), avaliacao.avaliadoEm(),
                avaliacao.fatores().stream().map(FatorRiscoResponse::de).toList());
    }
}
