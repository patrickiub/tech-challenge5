package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.fiap.vagazero.agenda.domain.Paciente;
import io.swagger.v3.oas.annotations.media.Schema;

public record PacienteResponse(
        @Schema(example = "2") Long id,
        @Schema(example = "Joao Pereira") String nome,
        @Schema(example = "700000000000012") String cns,
        @Schema(example = "11912345678") String telefone,
        @Schema(example = "-23.561684") BigDecimal latitude,
        @Schema(example = "-46.655981") BigDecimal longitude,
        @Schema(example = "1985-03-15") LocalDate dataNascimento) {

    public static PacienteResponse de(Paciente paciente) {
        return new PacienteResponse(
                paciente.id(), paciente.nome(), paciente.cns(), paciente.telefone(),
                paciente.latitude(), paciente.longitude(), paciente.dataNascimento());
    }
}
