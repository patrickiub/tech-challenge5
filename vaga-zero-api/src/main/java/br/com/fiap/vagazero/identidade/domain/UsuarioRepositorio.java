package br.com.fiap.vagazero.identidade.domain;

import java.util.Optional;

/**
 * Porta de persistencia do modulo identidade. Implementada na camada de
 * infraestrutura.
 */
public interface UsuarioRepositorio {

    boolean existePorEmail(String email);

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorEmail(String email);
}
