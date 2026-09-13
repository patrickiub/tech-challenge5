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

import br.com.fiap.vagazero.agenda.application.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @Operation(
            tags = "2 - Cadastros",
            summary = "Agendar consulta",
            description = "Requer uma vaga e um paciente existentes. Use pacienteId 1 (Maria Silva, ja "
                    + "cadastrada via migration) para nao depender de cadastrar um paciente antes.")
    @ApiResponse(responseCode = "201", description = "Agendamento criado")
    @ApiResponse(responseCode = "404", description = "Vaga ou paciente nao encontrado")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendamentoResponse criar(@Valid @RequestBody AgendamentoRequest requisicao) {
        var agendamento = agendamentoService.criar(requisicao.vagaId(), requisicao.pacienteId());
        return AgendamentoResponse.de(agendamento);
    }

    @Operation(tags = "2 - Cadastros", summary = "Buscar agendamento por id")
    @ApiResponse(responseCode = "404", description = "Agendamento nao encontrado")
    @GetMapping("/{id}")
    public AgendamentoResponse buscarPorId(@Parameter(example = "1") @PathVariable Long id) {
        return AgendamentoResponse.de(agendamentoService.buscarPorId(id));
    }

    @Operation(tags = "2 - Cadastros", summary = "Listar agendamentos")
    @GetMapping
    public List<AgendamentoResponse> listar() {
        return agendamentoService.listarTodos().stream().map(AgendamentoResponse::de).toList();
    }

    @Operation(
            tags = "4 - Cascata de vagas",
            summary = "Cancelar agendamento e disparar a cascata de convites",
            description = "Este e o gatilho da demonstracao: cancela o agendamento, publica "
                    + "agendamento.cancelado e vaga.liberada, e o motor de cascata assume a partir dai. "
                    + "Antes de executar, garanta que ha ao menos um paciente elegivel na fila de espera "
                    + "para a especialidade da vaga (endpoint 'Entrar na fila'), senao a vaga vai direto "
                    + "para PERDIDA. Em seguida, consulte 'Consultar estado da cascata' para acompanhar.")
    @ApiResponse(responseCode = "200", description = "Agendamento cancelado, vaga liberada para a cascata")
    @ApiResponse(responseCode = "404", description = "Agendamento nao encontrado")
    @ApiResponse(responseCode = "409", description = "Agendamento nao pode ser cancelado no status atual")
    @PostMapping("/{id}/cancelar")
    public AgendamentoResponse cancelar(@Parameter(example = "1") @PathVariable Long id) {
        return AgendamentoResponse.de(agendamentoService.cancelar(id));
    }

    @Operation(tags = "2 - Cadastros", summary = "Atualizar status do agendamento (uso administrativo)")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public AgendamentoResponse atualizarStatus(
            @Parameter(example = "1") @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusAgendamentoRequest requisicao) {
        var agendamento = agendamentoService.atualizarStatus(id, requisicao.status(), requisicao.confirmadoEm());
        return AgendamentoResponse.de(agendamento);
    }

    @Operation(tags = "2 - Cadastros", summary = "Excluir agendamento")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GESTOR')")
    public void excluir(@Parameter(example = "1") @PathVariable Long id) {
        agendamentoService.excluir(id);
    }
}
