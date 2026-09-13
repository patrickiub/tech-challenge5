package br.com.fiap.vagazero.shared.evento;

import java.time.LocalDateTime;

public record ConviteEnviadoEvento(
        Long conviteId, Long vagaId, Long pacienteId, LocalDateTime expiraEm, int ordemNaCascata) {
}
