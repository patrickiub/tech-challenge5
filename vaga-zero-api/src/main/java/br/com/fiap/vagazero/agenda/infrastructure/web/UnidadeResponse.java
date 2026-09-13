package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.math.BigDecimal;

import br.com.fiap.vagazero.agenda.domain.Unidade;
import io.swagger.v3.oas.annotations.media.Schema;

public record UnidadeResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "UBS Vila Mariana") String nome,
        @Schema(example = "-23.589000") BigDecimal latitude,
        @Schema(example = "-46.642000") BigDecimal longitude) {

    public static UnidadeResponse de(Unidade unidade) {
        return new UnidadeResponse(unidade.id(), unidade.nome(), unidade.latitude(), unidade.longitude());
    }
}
