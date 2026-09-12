package br.com.fiap.vagazero.agenda.domain;

import java.time.LocalDateTime;

public record Vaga(
        Long id,
        Long unidadeId,
        String especialidade,
        String profissional,
        LocalDateTime dataHora,
        StatusVaga status) {
}
