package br.com.fiap.vagazero.shared.evento;

public record ConviteExpiradoEvento(Long conviteId, Long vagaId, Long pacienteId, String motivo) {
}
