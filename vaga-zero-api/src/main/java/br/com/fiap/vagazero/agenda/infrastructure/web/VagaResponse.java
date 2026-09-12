package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.agenda.domain.StatusVaga;
import br.com.fiap.vagazero.agenda.domain.Vaga;

public record VagaResponse(
        Long id,
        Long unidadeId,
        String especialidade,
        String profissional,
        LocalDateTime dataHora,
        StatusVaga status) {

    public static VagaResponse de(Vaga vaga) {
        return new VagaResponse(
                vaga.id(), vaga.unidadeId(), vaga.especialidade(), vaga.profissional(),
                vaga.dataHora(), vaga.status());
    }
}
