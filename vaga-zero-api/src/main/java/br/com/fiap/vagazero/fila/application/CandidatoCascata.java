package br.com.fiap.vagazero.fila.application;

import br.com.fiap.vagazero.fila.domain.Convite;

/**
 * Linha de exibicao do estado da cascata. convite e null enquanto o
 * candidato ainda nao foi chamado (aguardando a vez).
 */
public record CandidatoCascata(
        int ordem, Long pacienteId, String nomePaciente, double distanciaKm, Convite convite) {
}
