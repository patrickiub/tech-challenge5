package br.com.fiap.vagazero.agenda.domain;

import java.time.LocalDateTime;

public record Agendamento(
        Long id,
        Long vagaId,
        Long pacienteId,
        StatusAgendamento status,
        LocalDateTime confirmadoEm) {
}
