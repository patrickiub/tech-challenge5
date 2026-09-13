package br.com.fiap.vagazero.agenda.application;

import br.com.fiap.vagazero.agenda.domain.StatusVaga;
import br.com.fiap.vagazero.agenda.domain.Vaga;

/**
 * Porto usado pelo modulo fila para consultar e transicionar o status de uma
 * vaga durante a cascata, sem acessar o repositorio de agenda diretamente.
 */
public interface VagaCascataUseCase {

    Vaga buscarPorId(Long id);

    /**
     * Transiciona o status da vaga apenas se o status atual em banco for o
     * esperado (UPDATE condicional atomico). Retorna false, sem lancar
     * excecao, quando a transicao ja nao é mais possivel (corrida concorrente
     * ou evento duplicado) - quem chama decide o que fazer com isso.
     */
    boolean mudarStatusSeAtual(Long id, StatusVaga statusEsperado, StatusVaga statusNovo);
}
