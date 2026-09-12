package br.com.fiap.vagazero.identidade.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.identidade.application.AutenticarUsuarioUseCase;
import br.com.fiap.vagazero.identidade.application.RegistrarUsuarioUseCase;
import br.com.fiap.vagazero.identidade.domain.Usuario;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    public AuthController(
            RegistrarUsuarioUseCase registrarUsuarioUseCase,
            AutenticarUsuarioUseCase autenticarUsuarioUseCase) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrarUsuarioResponse registrar(@Valid @RequestBody RegistrarUsuarioRequest requisicao) {
        Usuario usuario = registrarUsuarioUseCase.registrar(
                requisicao.email(), requisicao.senha(), requisicao.perfil(), requisicao.pacienteId());
        return new RegistrarUsuarioResponse(usuario.id(), usuario.email(), usuario.perfil());
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest requisicao) {
        String token = autenticarUsuarioUseCase.autenticar(requisicao.email(), requisicao.senha());
        return new LoginResponse(token);
    }
}
