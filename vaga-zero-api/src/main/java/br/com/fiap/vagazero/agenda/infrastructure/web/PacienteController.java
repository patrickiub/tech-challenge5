package br.com.fiap.vagazero.agenda.infrastructure.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.agenda.application.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "2 - Cadastros")
@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @Operation(summary = "Cadastrar paciente (gestao administrativa)",
            description = "Uso do GESTOR para cadastrar um paciente sem precisar registra-lo como usuario. "
                    + "O paciente de demonstracao Maria Silva (id 1) ja existe via migration - use este "
                    + "endpoint para cadastrar um segundo paciente.")
    @ApiResponse(responseCode = "201", description = "Paciente criado")
    @ApiResponse(responseCode = "409", description = "CNS ja cadastrado")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GESTOR')")
    public PacienteResponse criar(@Valid @RequestBody PacienteRequest requisicao) {
        var paciente = pacienteService.criar(
                requisicao.nome(), requisicao.cns(), requisicao.telefone(),
                requisicao.latitude(), requisicao.longitude(), requisicao.dataNascimento());
        return PacienteResponse.de(paciente);
    }

    @Operation(summary = "Buscar paciente por id")
    @ApiResponse(responseCode = "404", description = "Paciente nao encontrado")
    @GetMapping("/{id}")
    public PacienteResponse buscarPorId(@Parameter(example = "1") @PathVariable Long id) {
        return PacienteResponse.de(pacienteService.buscarPorId(id));
    }

    @Operation(summary = "Listar pacientes")
    @GetMapping
    public List<PacienteResponse> listar() {
        return pacienteService.listarTodos().stream().map(PacienteResponse::de).toList();
    }

    @Operation(summary = "Atualizar paciente")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public PacienteResponse atualizar(
            @Parameter(example = "1") @PathVariable Long id, @Valid @RequestBody PacienteRequest requisicao) {
        var paciente = pacienteService.atualizar(
                id, requisicao.nome(), requisicao.telefone(),
                requisicao.latitude(), requisicao.longitude(), requisicao.dataNascimento());
        return PacienteResponse.de(paciente);
    }

    @Operation(summary = "Excluir paciente")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GESTOR')")
    public void excluir(@Parameter(example = "1") @PathVariable Long id) {
        pacienteService.excluir(id);
    }
}
