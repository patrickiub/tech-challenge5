package br.com.fiap.vagazero.agenda.domain;

import java.util.List;
import java.util.Optional;

public interface VagaRepositorio {

    Vaga salvar(Vaga vaga);

    Optional<Vaga> buscarPorId(Long id);

    List<Vaga> listarTodas();

    void excluir(Long id);

    /**
     * Transicao atomica: so aplica se o status em banco ainda for o esperado.
     * Retorna true se a linha foi de fato alterada.
     */
    boolean mudarStatusSeAtual(Long id, StatusVaga statusEsperado, StatusVaga statusNovo);
}
