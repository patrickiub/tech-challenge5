package br.com.fiap.vagazero.agenda.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Paciente(
        Long id,
        String nome,
        String cns,
        String telefone,
        BigDecimal latitude,
        BigDecimal longitude,
        LocalDate dataNascimento) {
}
