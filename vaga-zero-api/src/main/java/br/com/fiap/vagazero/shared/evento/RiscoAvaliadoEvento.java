package br.com.fiap.vagazero.shared.evento;

public record RiscoAvaliadoEvento(Long avaliacaoId, Long agendamentoId, int score, String classificacao) {
}
