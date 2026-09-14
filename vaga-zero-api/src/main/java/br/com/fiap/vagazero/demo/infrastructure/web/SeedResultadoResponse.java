package br.com.fiap.vagazero.demo.infrastructure.web;

import br.com.fiap.vagazero.demo.application.SeedResultado;

public record SeedResultadoResponse(
        Long unidadeId,
        int totalVagas,
        int totalPacientesNaFila,
        Long vagaParaCancelarId,
        Long agendamentoParaCancelarId,
        Long pacienteParaCancelarId,
        String instrucaoCascataManual,
        Long vagaAltoRiscoId,
        Long agendamentoAltoRiscoId,
        Long pacienteAltoRiscoId,
        String instrucaoJobRiscoD2,
        String aviso) {

    public static SeedResultadoResponse de(SeedResultado resultado) {
        return new SeedResultadoResponse(
                resultado.unidadeId(), resultado.totalVagas(), resultado.totalPacientesNaFila(),
                resultado.vagaParaCancelarId(), resultado.agendamentoParaCancelarId(),
                resultado.pacienteParaCancelarId(), resultado.instrucaoCascataManual(),
                resultado.vagaAltoRiscoId(), resultado.agendamentoAltoRiscoId(), resultado.pacienteAltoRiscoId(),
                resultado.instrucaoJobRiscoD2(), resultado.aviso());
    }
}
