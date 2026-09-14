package br.com.fiap.vagazero.fila.domain;

import java.util.List;

public interface AvisoPendenteRepositorio {

    AvisoPendente salvar(AvisoPendente aviso);

    List<AvisoPendente> listarPendentes();

    void excluirTudo();
}
