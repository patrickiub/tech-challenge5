package br.com.fiap.vagazero.fila.infrastructure.web;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import br.com.fiap.vagazero.agenda.domain.StatusVaga;
import br.com.fiap.vagazero.fila.application.EstadoCascata;

public record CascataEstadoResponse(
        Long vagaId,
        String especialidade,
        StatusVaga statusVaga,
        LocalDateTime dataHoraVaga,
        List<CandidatoCascataResponse> candidatos) {

    public static CascataEstadoResponse de(EstadoCascata estado, Clock clock) {
        List<CandidatoCascataResponse> candidatos = estado.candidatos().stream()
                .map(candidato -> CandidatoCascataResponse.de(candidato, clock))
                .toList();
        return new CascataEstadoResponse(
                estado.vaga().id(), estado.vaga().especialidade(), estado.vaga().status(),
                estado.vaga().dataHora(), candidatos);
    }
}
