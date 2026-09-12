package br.com.fiap.vagazero.identidade.application;

import br.com.fiap.vagazero.identidade.domain.Perfil;
import br.com.fiap.vagazero.identidade.domain.Usuario;

public interface RegistrarUsuarioUseCase {

    Usuario registrar(String email, String senha, Perfil perfil, Long pacienteId);
}
