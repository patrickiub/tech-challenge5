package br.com.fiap.vagazero.risco.domain;

import java.util.Optional;

public interface AvaliacaoRiscoRepositorio {

    AvaliacaoRisco salvar(AvaliacaoRisco avaliacao);

    Optional<AvaliacaoRisco> buscarUltimaPorAgendamento(Long agendamentoId);
}
