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
import jakarta.validation.Valid;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GESTOR')")
    public PacienteResponse criar(@Valid @RequestBody PacienteRequest requisicao) {
        var paciente = pacienteService.criar(
                requisicao.nome(), requisicao.cns(), requisicao.telefone(),
                requisicao.latitude(), requisicao.longitude(), requisicao.dataNascimento());
        return PacienteResponse.de(paciente);
    }

    @GetMapping("/{id}")
    public PacienteResponse buscarPorId(@PathVariable Long id) {
        return PacienteResponse.de(pacienteService.buscarPorId(id));
    }

    @GetMapping
    public List<PacienteResponse> listar() {
        return pacienteService.listarTodos().stream().map(PacienteResponse::de).toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public PacienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody PacienteRequest requisicao) {
        var paciente = pacienteService.atualizar(
                id, requisicao.nome(), requisicao.telefone(),
                requisicao.latitude(), requisicao.longitude(), requisicao.dataNascimento());
        return PacienteResponse.de(paciente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GESTOR')")
    public void excluir(@PathVariable Long id) {
        pacienteService.excluir(id);
    }
}
