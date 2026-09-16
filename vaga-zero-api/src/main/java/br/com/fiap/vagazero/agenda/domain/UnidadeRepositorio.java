package br.com.fiap.vagazero.agenda.domain;

import java.util.List;
import java.util.Optional;

public interface UnidadeRepositorio {

    Unidade salvar(Unidade unidade);

    Optional<Unidade> buscarPorId(Long id);

    List<Unidade> listarTodas();

    void excluir(Long id);

    boolean existePorId(Long id);
}
