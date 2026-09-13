package br.com.fiap.vagazero.fila.infrastructure.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.agenda.application.ConsultaPacienteUseCase;
import br.com.fiap.vagazero.fila.application.FilaService;
import br.com.fiap.vagazero.fila.domain.ItemFila;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "3 - Fila de espera", description = "Cadastro dos candidatos que aguardam vaga. E aqui que o paciente entra elegivel para ser chamado quando uma vaga for liberada.")
@RestController
@RequestMapping("/fila")
public class FilaController {

    private final FilaService filaService;
    private final ConsultaPacienteUseCase consultaPacienteUseCase;

    public FilaController(FilaService filaService, ConsultaPacienteUseCase consultaPacienteUseCase) {
        this.filaService = filaService;
        this.consultaPacienteUseCase = consultaPacienteUseCase;
    }

    @Operation(summary = "Entrar na fila de espera",
            description = "Execute antes de cancelar um agendamento na secao '4 - Cascata de vagas': sem "
                    + "ninguem elegivel na fila, a vaga liberada vai direto para PERDIDA.")
    @ApiResponse(responseCode = "201", description = "Paciente inserido na fila")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemFilaResponse entrar(@Valid @RequestBody ItemFilaRequest requisicao) {
        var item = filaService.entrar(
                requisicao.pacienteId(), requisicao.especialidade(), requisicao.prioridadeClinica(),
                requisicao.aceitaChamadoImediato(), requisicao.raioMaximoKm());
        return responder(item);
    }

    @Operation(summary = "Buscar item da fila por id")
    @ApiResponse(responseCode = "404", description = "Item de fila nao encontrado")
    @GetMapping("/{id}")
    public ItemFilaResponse buscarPorId(@Parameter(example = "1") @PathVariable Long id) {
        return responder(filaService.buscarPorId(id));
    }

    @Operation(summary = "Listar fila de espera",
            description = "Filtre por especialidade para ver a mesma populacao candidata que o motor de "
                    + "cascata usa (antes da ordenacao por prioridade/tempo de espera/distancia).")
    @GetMapping
    public List<ItemFilaResponse> listar(
            @Parameter(example = "Oftalmologia") @RequestParam(required = false) String especialidade) {
        return filaService.listar(especialidade).stream().map(this::responder).toList();
    }

    @Operation(summary = "Remover paciente da fila",
            description = "Retorna o paciente removido, a especialidade e quantos pacientes restam na fila "
                    + "dessa especialidade, em vez de um corpo vazio.")
    @ApiResponse(responseCode = "200", description = "Paciente removido da fila")
    @ApiResponse(responseCode = "404", description = "Item de fila nao encontrado")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public ItemFilaRemovidoResponse sair(@Parameter(example = "1") @PathVariable Long id) {
        ItemFila item = filaService.buscarPorId(id);
        String nomePaciente = consultaPacienteUseCase.buscarResumo(item.pacienteId()).nome();
        filaService.sair(id);
        int restantes = filaService.listar(item.especialidade()).size();
        return new ItemFilaRemovidoResponse(item.pacienteId(), nomePaciente, item.especialidade(), true, restantes);
    }

    private ItemFilaResponse responder(ItemFila item) {
        String nomePaciente = consultaPacienteUseCase.buscarResumo(item.pacienteId()).nome();
        return ItemFilaResponse.de(item, nomePaciente);
    }
}
