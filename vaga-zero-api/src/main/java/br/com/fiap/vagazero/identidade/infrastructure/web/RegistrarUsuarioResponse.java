package br.com.fiap.vagazero.identidade.infrastructure.web;

import br.com.fiap.vagazero.identidade.domain.Perfil;

public record RegistrarUsuarioResponse(Long id, String email, Perfil perfil) {
}
