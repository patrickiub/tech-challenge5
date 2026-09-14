package br.com.fiap.vagazero.demo.infrastructure.web;

import br.com.fiap.vagazero.demo.application.SeedResultado;

public record SeedResultadoResponse(
        Long unidadeId,
        int totalVagas,
        int totalPacientesNaFila,
        Long agendamentoParaCancelarId,
        Long pacienteParaCancelarId,
        String instrucaoCascataManual,
        Long agendamentoAltoRiscoId,
        Long pacienteAltoRiscoId,
        String instrucaoJobRiscoD2,
        String aviso) {

    public static SeedResultadoResponse de(SeedResultado resultado) {
        return new SeedResultadoResponse(
                resultado.unidadeId(), resultado.totalVagas(), resultado.totalPacientesNaFila(),
                resultado.agendamentoParaCancelarId(), resultado.pacienteParaCancelarId(),
                resultado.instrucaoCascataManual(), resultado.agendamentoAltoRiscoId(),
                resultado.pacienteAltoRiscoId(), resultado.instrucaoJobRiscoD2(), resultado.aviso());
    }
}
