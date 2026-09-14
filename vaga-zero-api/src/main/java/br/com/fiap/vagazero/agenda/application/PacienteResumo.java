package br.com.fiap.vagazero.agenda.application;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PacienteResumo(
        Long id, String nome, BigDecimal latitude, BigDecimal longitude, LocalDate dataNascimento) {
}
