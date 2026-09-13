package br.com.fiap.vagazero.shared.evento;

import java.time.LocalDateTime;

public record VagaLiberadaEvento(Long vagaId, String especialidade, Long unidadeId, LocalDateTime dataHora) {
}
