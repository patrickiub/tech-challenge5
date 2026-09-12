package br.com.fiap.vagazero.agenda.domain;

import java.math.BigDecimal;

public record Unidade(Long id, String nome, BigDecimal latitude, BigDecimal longitude) {
}
