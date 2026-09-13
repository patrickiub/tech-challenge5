package br.com.fiap.vagazero.fila.infrastructure.web;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

import br.com.fiap.vagazero.fila.application.CandidatoCascata;
import br.com.fiap.vagazero.fila.domain.Convite;
import io.swagger.v3.oas.annotations.media.Schema;

public record CandidatoCascataResponse(
        @Schema(example = "1", description = "Posicao do candidato na ordem da cascata") int ordem,
        @Schema(example = "1") Long pacienteId,
        @Schema(example = "Maria da Silva") String nomePaciente,
        @Schema(example = "2.3") double distanciaKm,
        @Schema(example = "ENVIADO", description = "AGUARDANDO_VEZ (ainda nao chamado), ENVIADO, ACEITO, "
                + "RECUSADO ou EXPIRADO")
        String statusConvite,
        @Schema(example = "2026-09-13T10:00:00") LocalDateTime enviadoEm,
        @Schema(example = "2026-09-13T10:30:00") LocalDateTime expiraEm,
        @Schema(example = "1200", description = "Segundos restantes do convite ativo (null se nao ENVIADO)")
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
