package br.com.fiap.vagazero.fila.infrastructure.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.vagazero.fila.domain.ItemFila;

public record ItemFilaResponse(
        Long id,
        Long pacienteId,
        String especialidade,
        LocalDateTime dataEntrada,
        int prioridadeClinica,
        boolean aceitaChamadoImediato,
        BigDecimal raioMaximoKm) {

    public static ItemFilaResponse de(ItemFila item) {
        return new ItemFilaResponse(
                item.id(), item.pacienteId(), item.especialidade(), item.dataEntrada(),
                item.prioridadeClinica(), item.aceitaChamadoImediato(), item.raioMaximoKm());
    }
}
