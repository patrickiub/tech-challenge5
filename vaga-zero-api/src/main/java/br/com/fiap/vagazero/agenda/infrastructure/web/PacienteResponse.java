package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.fiap.vagazero.agenda.domain.Paciente;

public record PacienteResponse(
        Long id,
        String nome,
        String cns,
        String telefone,
        BigDecimal latitude,
        BigDecimal longitude,
        LocalDate dataNascimento) {

    public static PacienteResponse de(Paciente paciente) {
        return new PacienteResponse(
                paciente.id(), paciente.nome(), paciente.cns(), paciente.telefone(),
                paciente.latitude(), paciente.longitude(), paciente.dataNascimento());
    }
}
