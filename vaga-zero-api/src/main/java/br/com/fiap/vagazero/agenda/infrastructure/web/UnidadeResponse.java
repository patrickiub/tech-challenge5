package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.math.BigDecimal;

import br.com.fiap.vagazero.agenda.domain.Unidade;

public record UnidadeResponse(Long id, String nome, BigDecimal latitude, BigDecimal longitude) {

    public static UnidadeResponse de(Unidade unidade) {
        return new UnidadeResponse(unidade.id(), unidade.nome(), unidade.latitude(), unidade.longitude());
    }
}
