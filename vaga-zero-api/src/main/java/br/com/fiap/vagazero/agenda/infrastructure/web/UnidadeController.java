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

import br.com.fiap.vagazero.agenda.application.UnidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "2 - Cadastros", description = "Unidades, pacientes, vagas e agendamentos - a base de dados sobre a qual a fila e a cascata operam.")
@RestController
@RequestMapping("/unidades")
public class UnidadeController {

    private final UnidadeService unidadeService;

    public UnidadeController(UnidadeService unidadeService) {
        this.unidadeService = unidadeService;
    }

    @Operation(summary = "Cadastrar unidade de saude",
            description = "Execute primeiro: a vaga criada em seguida referencia o id gerado aqui.")
    @ApiResponse(responseCode = "201", description = "Unidade criada")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GESTOR')")
    public UnidadeResponse criar(@Valid @RequestBody UnidadeRequest requisicao) {
        var unidade = unidadeService.criar(requisicao.nome(), requisicao.latitude(), requisicao.longitude());
        return UnidadeResponse.de(unidade);
    }

    @Operation(summary = "Buscar unidade por id")
    @ApiResponse(responseCode = "404", description = "Unidade nao encontrada")
    @GetMapping("/{id}")
    public UnidadeResponse buscarPorId(@Parameter(example = "1") @PathVariable Long id) {
        return UnidadeResponse.de(unidadeService.buscarPorId(id));
    }

    @Operation(summary = "Listar unidades")
    @GetMapping
    public List<UnidadeResponse> listar() {
        return unidadeService.listarTodas().stream().map(UnidadeResponse::de).toList();
    }

    @Operation(summary = "Atualizar unidade")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public UnidadeResponse atualizar(
            @Parameter(example = "1") @PathVariable Long id, @Valid @RequestBody UnidadeRequest requisicao) {
        var unidade = unidadeService.atualizar(id, requisicao.nome(), requisicao.latitude(), requisicao.longitude());
        return UnidadeResponse.de(unidade);
    }

    @Operation(summary = "Excluir unidade")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GESTOR')")
    public void excluir(@Parameter(example = "1") @PathVariable Long id) {
        unidadeService.excluir(id);
    }
}
