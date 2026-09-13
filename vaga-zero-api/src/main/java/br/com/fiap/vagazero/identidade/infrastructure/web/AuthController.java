package br.com.fiap.vagazero.identidade.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.identidade.application.AutenticarUsuarioUseCase;
import br.com.fiap.vagazero.identidade.application.DadosPacienteRegistro;
import br.com.fiap.vagazero.identidade.application.RegistrarUsuarioUseCase;
import br.com.fiap.vagazero.identidade.domain.Perfil;
import br.com.fiap.vagazero.identidade.domain.Usuario;
import br.com.fiap.vagazero.shared.web.ErroResposta;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "1 - Autenticacao", description = "Login e auto-cadastro. Comece por aqui: faca login, copie o token e clique em Authorize.")
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

    @SecurityRequirements
    @Operation(
            summary = "Registrar novo usuario (PACIENTE ou GESTOR)",
            description = "Para PACIENTE, cria o paciente na mesma transacao a partir dos dados clinicos "
                    + "informados aqui - nao existe vinculo por id de paciente pre-existente. Para GESTOR, "
                    + "os campos clinicos devem ficar vazios. Os usuarios de demonstracao (gestor@vagazero.com "
                    + "e maria.silva@email.com) ja existem via migration, entao este endpoint e opcional para "
                    + "a demonstracao principal - use-o para mostrar o auto-cadastro de um novo paciente.")
    @ApiResponse(responseCode = "201", description = "Usuario criado")
    @ApiResponse(responseCode = "400", description = "Dados invalidos ou incoerentes com o perfil", content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = "{\"mensagem\": \"Perfil GESTOR nao deve informar dados de paciente "
                    + "(nome, cns, telefone, dataNascimento, latitude, longitude)\"}")))
    @ApiResponse(responseCode = "409", description = "Email ou CNS ja cadastrado", content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = "{\"mensagem\": \"Ja existe um usuario cadastrado com o email "
                    + "ana.costa@email.com\"}")))
    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrarUsuarioResponse registrar(@Valid @RequestBody RegistrarUsuarioRequest requisicao) {
        DadosPacienteRegistro dadosPaciente = requisicao.perfil() == Perfil.PACIENTE
                ? new DadosPacienteRegistro(
                        requisicao.nome(), requisicao.cns(), requisicao.telefone(), requisicao.dataNascimento(),
                        requisicao.latitude(), requisicao.longitude())
                : null;
        Usuario usuario = registrarUsuarioUseCase.registrar(
                requisicao.email(), requisicao.senha(), requisicao.perfil(), dadosPaciente);
        return RegistrarUsuarioResponse.de(usuario);
    }

    @SecurityRequirements
    @Operation(
            summary = "Login e emissao do token JWT",
            description = "Execute com o exemplo preenchido (gestor de demonstracao) para obter um token. "
                    + "Copie o valor de 'token' da resposta, clique no botao Authorize no topo da pagina e "
                    + "cole como 'Bearer <token>' (ou apenas o token, dependendo do esquema configurado) para "
                    + "liberar os demais endpoints.")
    @ApiResponse(responseCode = "200", description = "Autenticado com sucesso")
    @ApiResponse(responseCode = "401", description = "Email ou senha invalidos", content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(value = "{\"mensagem\": \"Email ou senha invalidos\"}")))
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest requisicao) {
        String token = autenticarUsuarioUseCase.autenticar(requisicao.email(), requisicao.senha());
        return new LoginResponse(token);
    }
}
