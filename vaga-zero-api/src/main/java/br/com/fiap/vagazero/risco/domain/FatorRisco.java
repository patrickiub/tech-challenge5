package br.com.fiap.vagazero.risco.domain;

/**
 * Um fator aplicado ao score de uma avaliacao de risco - a explicabilidade
 * do motor de regras. So fatores que de fato contribuiram (pontos != 0)
 * entram na lista.
 */
public record FatorRisco(String codigo, String descricao, int pontos) {
}
