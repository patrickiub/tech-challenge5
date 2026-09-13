package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record PacienteRequest(
        @NotBlank @Schema(example = "Joao Pereira") String nome,
        @NotBlank @Size(min = 15, max = 15) @Schema(example = "700000000000012") String cns,
        @Schema(example = "11912345678") String telefone,
        @NotNull @Schema(example = "-23.561684") BigDecimal latitude,
        @NotNull @Schema(example = "-46.655981") BigDecimal longitude,
        @NotNull @Past @Schema(example = "1985-03-15") LocalDate dataNascimento) {
}
