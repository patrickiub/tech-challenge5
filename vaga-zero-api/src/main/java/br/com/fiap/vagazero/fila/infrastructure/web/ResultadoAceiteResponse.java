package br.com.fiap.vagazero.fila.infrastructure.web;

import br.com.fiap.vagazero.fila.application.ResultadoAceite;

public record ResultadoAceiteResponse(Long vagaId, Long agendamentoId, Long pacienteId) {

    public static ResultadoAceiteResponse de(ResultadoAceite resultado) {
        return new ResultadoAceiteResponse(resultado.vagaId(), resultado.agendamentoId(), resultado.pacienteId());
    }
}
