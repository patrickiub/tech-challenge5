package br.com.fiap.vagazero.fila.domain;

import java.time.LocalDateTime;

public record Convite(
        Long id,
        Long vagaId,
        Long pacienteId,
        LocalDateTime enviadoEm,
        LocalDateTime expiraEm,
        StatusConvite status,
        int ordemNaCascata) {
}
