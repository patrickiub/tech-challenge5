package br.com.fiap.vagazero.identidade.infrastructure.web;

import br.com.fiap.vagazero.identidade.domain.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrarUsuarioRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String senha,
        @NotNull Perfil perfil,
        Long pacienteId) {
}
