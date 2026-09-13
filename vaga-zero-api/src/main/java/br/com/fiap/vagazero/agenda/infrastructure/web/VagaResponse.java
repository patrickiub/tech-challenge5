package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.agenda.domain.StatusVaga;
import br.com.fiap.vagazero.agenda.domain.Vaga;
import io.swagger.v3.oas.annotations.media.Schema;

public record VagaResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long unidadeId,
        @Schema(example = "UBS Vila Mariana") String unidadeNome,
        @Schema(example = "Oftalmologia") String especialidade,
        @Schema(example = "Dra. Beatriz Lima") String profissional,
        @Schema(example = "2026-10-15T09:00:00") LocalDateTime dataHora,
        @Schema(example = "DISPONIVEL") StatusVaga status) {

    public static VagaResponse de(Vaga vaga, String unidadeNome) {
        return new VagaResponse(
                vaga.id(), vaga.unidadeId(), unidadeNome, vaga.especialidade(), vaga.profissional(),
                vaga.dataHora(), vaga.status());
    }
}
