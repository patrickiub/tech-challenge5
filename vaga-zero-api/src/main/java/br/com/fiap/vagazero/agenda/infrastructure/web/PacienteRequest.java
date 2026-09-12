package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record PacienteRequest(
        @NotBlank String nome,
        @NotBlank @Size(min = 15, max = 15) String cns,
        String telefone,
        @NotNull BigDecimal latitude,
        @NotNull BigDecimal longitude,
        @NotNull @Past LocalDate dataNascimento) {
}
