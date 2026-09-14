package br.com.fiap.vagazero.risco.domain;

import java.time.LocalDateTime;
import java.util.List;

public record AvaliacaoRisco(
        Long id,
        Long agendamentoId,
        int score,
        ClassificacaoRisco classificacao,
        LocalDateTime avaliadoEm,
        List<FatorRisco> fatores) {
}
