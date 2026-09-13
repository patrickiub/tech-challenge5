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
import br.com.fiap.vagazero.agenda.application.VagaService;
import br.com.fiap.vagazero.agenda.domain.Vaga;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "2 - Cadastros")
@RestController
@RequestMapping("/vagas")
public class VagaController {

    private final VagaService vagaService;
    private final UnidadeService unidadeService;

    public VagaController(VagaService vagaService, UnidadeService unidadeService) {
        this.vagaService = vagaService;
        this.unidadeService = unidadeService;
    }

    @Operation(summary = "Cadastrar vaga de atendimento",
            description = "Requer uma unidade existente (execute 'Cadastrar unidade' antes). A vaga criada "
                    + "aqui e a que sera cancelada mais adiante para disparar a cascata de convites.")
    @ApiResponse(responseCode = "201", description = "Vaga criada")
    @ApiResponse(responseCode = "404", description = "Unidade nao encontrada")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GESTOR')")
    public VagaResponse criar(@Valid @RequestBody VagaRequest requisicao) {
        var vaga = vagaService.criar(
                requisicao.unidadeId(), requisicao.especialidade(), requisicao.profissional(), requisicao.dataHora());
        return responder(vaga);
    }

    @Operation(summary = "Buscar vaga por id")
    @ApiResponse(responseCode = "404", description = "Vaga nao encontrada")
    @GetMapping("/{id}")
    public VagaResponse buscarPorId(@Parameter(example = "1") @PathVariable Long id) {
        return responder(vagaService.buscarPorId(id));
    }

    @Operation(summary = "Listar vagas")
    @GetMapping
    public List<VagaResponse> listar() {
        return vagaService.listarTodas().stream().map(this::responder).toList();
    }

    @Operation(summary = "Atualizar vaga",
            description = "Edicao administrativa direta. Para liberar a vaga para a cascata, use "
                    + "'Cancelar agendamento' em vez de mudar o status aqui manualmente.")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public VagaResponse atualizar(
            @Parameter(example = "1") @PathVariable Long id, @Valid @RequestBody AtualizarVagaRequest requisicao) {
        var vaga = vagaService.atualizar(
                id, requisicao.especialidade(), requisicao.profissional(), requisicao.dataHora(),
                requisicao.status());
        return responder(vaga);
    }

    @Operation(summary = "Excluir vaga",
            description = "Retorna a vaga removida e quantas vagas restam cadastradas, em vez de um corpo "
                    + "vazio.")
    @ApiResponse(responseCode = "200", description = "Vaga removida")
    @ApiResponse(responseCode = "404", description = "Vaga nao encontrada")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public VagaRemovidaResponse excluir(@Parameter(example = "1") @PathVariable Long id) {
        var vaga = vagaService.buscarPorId(id);
        String unidadeNome = unidadeService.buscarPorId(vaga.unidadeId()).nome();
        vagaService.excluir(id);
        int totalRestante = vagaService.listarTodas().size();
        return new VagaRemovidaResponse(vaga.id(), vaga.especialidade(), unidadeNome, true, totalRestante);
    }

    private VagaResponse responder(Vaga vaga) {
        String unidadeNome = unidadeService.buscarPorId(vaga.unidadeId()).nome();
        return VagaResponse.de(vaga, unidadeNome);
    }
}
