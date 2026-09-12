package br.com.fiap.vagazero.agenda.domain;

import java.util.List;
import java.util.Optional;

public interface VagaRepositorio {

    Vaga salvar(Vaga vaga);

    Optional<Vaga> buscarPorId(Long id);

    List<Vaga> listarTodas();

    void excluir(Long id);
}
