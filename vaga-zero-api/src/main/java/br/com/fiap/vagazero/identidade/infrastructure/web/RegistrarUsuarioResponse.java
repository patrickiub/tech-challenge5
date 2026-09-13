package br.com.fiap.vagazero.identidade.infrastructure.web;

import br.com.fiap.vagazero.identidade.domain.Perfil;
import br.com.fiap.vagazero.identidade.domain.Usuario;
import io.swagger.v3.oas.annotations.media.Schema;

public record RegistrarUsuarioResponse(
        @Schema(example = "3") Long id,
        @Schema(example = "ana.costa@email.com") String email,
        @Schema(example = "PACIENTE") Perfil perfil,
        @Schema(example = "2", description = "Preenchido apenas para perfil PACIENTE") Long pacienteId) {

    public static RegistrarUsuarioResponse de(Usuario usuario) {
        return new RegistrarUsuarioResponse(usuario.id(), usuario.email(), usuario.perfil(), usuario.pacienteId());
    }
}
