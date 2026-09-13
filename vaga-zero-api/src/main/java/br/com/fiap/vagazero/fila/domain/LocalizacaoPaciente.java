package br.com.fiap.vagazero.fila.domain;

import java.math.BigDecimal;

/**
 * Dados do paciente necessarios para calcular elegibilidade/distancia na
 * cascata. Mantido no dominio da fila (em vez de reusar o tipo de agenda)
 * para nao acoplar o dominio de um modulo ao de outro.
 */
public record LocalizacaoPaciente(Long pacienteId, String nome, BigDecimal latitude, BigDecimal longitude) {
}
