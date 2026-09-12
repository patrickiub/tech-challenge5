package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.agenda.domain.Agendamento;
import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;

public record AgendamentoResponse(
        Long id, Long vagaId, Long pacienteId, StatusAgendamento status, LocalDateTime confirmadoEm) {

    public static AgendamentoResponse de(Agendamento agendamento) {
        return new AgendamentoResponse(
                agendamento.id(), agendamento.vagaId(), agendamento.pacienteId(),
                agendamento.status(), agendamento.confirmadoEm());
    }
}
