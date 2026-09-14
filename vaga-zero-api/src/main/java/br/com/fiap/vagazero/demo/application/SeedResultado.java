package br.com.fiap.vagazero.demo.application;

public record SeedResultado(
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
}
