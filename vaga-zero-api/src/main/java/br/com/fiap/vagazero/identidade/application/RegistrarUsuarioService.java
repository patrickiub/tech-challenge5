package br.com.fiap.vagazero.identidade.application;

import org.springframework.stereotype.Service;

import br.com.fiap.vagazero.identidade.domain.CodificadorSenha;
import br.com.fiap.vagazero.identidade.domain.EmailJaCadastradoException;
import br.com.fiap.vagazero.identidade.domain.Perfil;
import br.com.fiap.vagazero.identidade.domain.Usuario;
import br.com.fiap.vagazero.identidade.domain.UsuarioRepositorio;

@Service
public class RegistrarUsuarioService implements RegistrarUsuarioUseCase {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CodificadorSenha codificadorSenha;

    public RegistrarUsuarioService(UsuarioRepositorio usuarioRepositorio, CodificadorSenha codificadorSenha) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.codificadorSenha = codificadorSenha;
    }

    @Override
    public Usuario registrar(String email, String senha, Perfil perfil, Long pacienteId) {
        if (usuarioRepositorio.existePorEmail(email)) {
            throw new EmailJaCadastradoException(email);
        }
        String senhaHash = codificadorSenha.codificar(senha);
        Usuario usuario = new Usuario(null, email, senhaHash, perfil, pacienteId);
        return usuarioRepositorio.salvar(usuario);
    }
}
