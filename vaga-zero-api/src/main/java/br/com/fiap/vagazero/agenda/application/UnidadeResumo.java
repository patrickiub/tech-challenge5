package br.com.fiap.vagazero.agenda.application;

import java.math.BigDecimal;

public record UnidadeResumo(Long id, String nome, BigDecimal latitude, BigDecimal longitude) {
}
