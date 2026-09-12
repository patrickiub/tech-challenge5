package br.com.fiap.vagazero.identidade.domain;

public record Usuario(Long id, String email, String senhaHash, Perfil perfil, Long pacienteId) {
}
