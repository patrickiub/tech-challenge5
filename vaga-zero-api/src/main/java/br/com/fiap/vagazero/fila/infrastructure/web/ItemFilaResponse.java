package br.com.fiap.vagazero.fila.infrastructure.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.fiap.vagazero.fila.domain.ItemFila;
import io.swagger.v3.oas.annotations.media.Schema;

public record ItemFilaResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long pacienteId,
        @Schema(example = "Maria da Silva") String nomePaciente,
        @Schema(example = "Oftalmologia") String especialidade,
        @Schema(example = "2026-09-13T10:00:00") LocalDateTime dataEntrada,
        @Schema(example = "3") int prioridadeClinica,
        @Schema(example = "true") boolean aceitaChamadoImediato,
        @Schema(example = "15.0") BigDecimal raioMaximoKm) {

    public static ItemFilaResponse de(ItemFila item, String nomePaciente) {
        return new ItemFilaResponse(
                item.id(), item.pacienteId(), nomePaciente, item.especialidade(), item.dataEntrada(),
                item.prioridadeClinica(), item.aceitaChamadoImediato(), item.raioMaximoKm());
    }
}
