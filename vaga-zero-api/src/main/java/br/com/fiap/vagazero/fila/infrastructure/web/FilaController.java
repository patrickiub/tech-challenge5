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

import br.com.fiap.vagazero.fila.application.FilaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/fila")
public class FilaController {

    private final FilaService filaService;

    public FilaController(FilaService filaService) {
        this.filaService = filaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemFilaResponse entrar(@Valid @RequestBody ItemFilaRequest requisicao) {
        var item = filaService.entrar(
                requisicao.pacienteId(), requisicao.especialidade(), requisicao.prioridadeClinica(),
                requisicao.aceitaChamadoImediato(), requisicao.raioMaximoKm());
        return ItemFilaResponse.de(item);
    }

    @GetMapping("/{id}")
    public ItemFilaResponse buscarPorId(@PathVariable Long id) {
        return ItemFilaResponse.de(filaService.buscarPorId(id));
    }

    @GetMapping
    public List<ItemFilaResponse> listar(@RequestParam(required = false) String especialidade) {
        return filaService.listar(especialidade).stream().map(ItemFilaResponse::de).toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GESTOR')")
    public void sair(@PathVariable Long id) {
        filaService.sair(id);
    }
}
