package br.com.fiap.vagazero.fila.application;

import java.util.List;

import br.com.fiap.vagazero.agenda.domain.Vaga;

public record EstadoCascata(Vaga vaga, List<CandidatoCascata> candidatos) {
}
