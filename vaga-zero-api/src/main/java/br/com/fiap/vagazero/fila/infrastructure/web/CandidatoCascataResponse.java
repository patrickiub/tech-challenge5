package br.com.fiap.vagazero.fila.infrastructure.web;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

import br.com.fiap.vagazero.fila.application.CandidatoCascata;
import br.com.fiap.vagazero.fila.domain.Convite;

public record CandidatoCascataResponse(
        int ordem,
        Long pacienteId,
        String nomePaciente,
        double distanciaKm,
        String statusConvite,
        LocalDateTime enviadoEm,
        LocalDateTime expiraEm,
        Long segundosRestantes) {

    private static final String AGUARDANDO_VEZ = "AGUARDANDO_VEZ";

    public static CandidatoCascataResponse de(CandidatoCascata candidato, Clock clock) {
        Convite convite = candidato.convite();
        if (convite == null) {
            return new CandidatoCascataResponse(
                    candidato.ordem(), candidato.pacienteId(), candidato.nomePaciente(),
                    arredondar(candidato.distanciaKm()), AGUARDANDO_VEZ, null, null, null);
        }
        Long segundosRestantes = "ENVIADO".equals(convite.status().name())
                ? Math.max(0, Duration.between(LocalDateTime.now(clock), convite.expiraEm()).getSeconds())
                : null;
        return new CandidatoCascataResponse(
                candidato.ordem(), candidato.pacienteId(), candidato.nomePaciente(),
                arredondar(candidato.distanciaKm()), convite.status().name(), convite.enviadoEm(),
                convite.expiraEm(), segundosRestantes);
    }

    private static double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
