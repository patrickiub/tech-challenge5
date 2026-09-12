package br.com.fiap.vagazero.identidade.application;

import org.springframework.stereotype.Service;

import br.com.fiap.vagazero.identidade.domain.CodificadorSenha;
import br.com.fiap.vagazero.identidade.domain.CredenciaisInvalidasException;
import br.com.fiap.vagazero.identidade.domain.GeradorTokenJwt;
import br.com.fiap.vagazero.identidade.domain.Usuario;
import br.com.fiap.vagazero.identidade.domain.UsuarioRepositorio;

@Service
public class AutenticarUsuarioService implements AutenticarUsuarioUseCase {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CodificadorSenha codificadorSenha;
    private final GeradorTokenJwt geradorTokenJwt;

    public AutenticarUsuarioService(
            UsuarioRepositorio usuarioRepositorio,
            CodificadorSenha codificadorSenha,
            GeradorTokenJwt geradorTokenJwt) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.codificadorSenha = codificadorSenha;
        this.geradorTokenJwt = geradorTokenJwt;
    }

    @Override
    public String autenticar(String email, String senha) {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email)
                .orElseThrow(CredenciaisInvalidasException::new);
        if (!codificadorSenha.confere(senha, usuario.senhaHash())) {
            throw new CredenciaisInvalidasException();
        }
        return geradorTokenJwt.gerar(usuario);
    }
}
