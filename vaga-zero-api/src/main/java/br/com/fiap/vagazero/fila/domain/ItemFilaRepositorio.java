package br.com.fiap.vagazero.fila.domain;

import java.util.List;
import java.util.Optional;

public interface ItemFilaRepositorio {

    ItemFila salvar(ItemFila itemFila);

    Optional<ItemFila> buscarPorId(Long id);

    List<ItemFila> listarTodos();

    List<ItemFila> listarPorEspecialidade(String especialidade);

    Optional<ItemFila> buscarPorPacienteEEspecialidade(Long pacienteId, String especialidade);

    void excluir(Long id);
}
