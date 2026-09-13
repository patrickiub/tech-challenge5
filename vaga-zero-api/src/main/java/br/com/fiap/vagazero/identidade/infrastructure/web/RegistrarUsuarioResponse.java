package br.com.fiap.vagazero.identidade.infrastructure.web;

import br.com.fiap.vagazero.identidade.domain.Perfil;
import br.com.fiap.vagazero.identidade.domain.Usuario;

public record RegistrarUsuarioResponse(Long id, String email, Perfil perfil, Long pacienteId) {

    public static RegistrarUsuarioResponse de(Usuario usuario) {
        return new RegistrarUsuarioResponse(usuario.id(), usuario.email(), usuario.perfil(), usuario.pacienteId());
    }
}
