package br.com.fiap.vagazero.risco.infrastructure.web;

import br.com.fiap.vagazero.risco.domain.FatorRisco;
import io.swagger.v3.oas.annotations.media.Schema;

public record FatorRiscoResponse(
        @Schema(example = "FALTAS_RECENTES") String codigo,
        @Schema(example = "2 falta(s) nos ultimos 12 meses") String descricao,
        @Schema(example = "50") int pontos) {

    public static FatorRiscoResponse de(FatorRisco fator) {
        return new FatorRiscoResponse(fator.codigo(), fator.descricao(), fator.pontos());
    }
}
