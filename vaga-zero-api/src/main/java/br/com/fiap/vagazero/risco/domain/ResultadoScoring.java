package br.com.fiap.vagazero.risco.domain;

import java.util.List;

public record ResultadoScoring(int score, ClassificacaoRisco classificacao, List<FatorRisco> fatores) {
}
