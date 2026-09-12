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
import jakarta.validation.Valid;

@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendamentoResponse criar(@Valid @RequestBody AgendamentoRequest requisicao) {
        var agendamento = agendamentoService.criar(requisicao.vagaId(), requisicao.pacienteId());
        return AgendamentoResponse.de(agendamento);
    }

    @GetMapping("/{id}")
    public AgendamentoResponse buscarPorId(@PathVariable Long id) {
        return AgendamentoResponse.de(agendamentoService.buscarPorId(id));
    }

    @GetMapping
    public List<AgendamentoResponse> listar() {
        return agendamentoService.listarTodos().stream().map(AgendamentoResponse::de).toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public AgendamentoResponse atualizarStatus(
            @PathVariable Long id, @Valid @RequestBody AtualizarStatusAgendamentoRequest requisicao) {
        var agendamento = agendamentoService.atualizarStatus(id, requisicao.status(), requisicao.confirmadoEm());
        return AgendamentoResponse.de(agendamento);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GESTOR')")
    public void excluir(@PathVariable Long id) {
        agendamentoService.excluir(id);
    }
}
