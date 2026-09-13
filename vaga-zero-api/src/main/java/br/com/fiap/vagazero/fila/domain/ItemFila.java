package br.com.fiap.vagazero.fila.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemFila(
        Long id,
        Long pacienteId,
        String especialidade,
        LocalDateTime dataEntrada,
        int prioridadeClinica,
        boolean aceitaChamadoImediato,
        BigDecimal raioMaximoKm) {
}
