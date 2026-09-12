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
import jakarta.validation.Valid;

@RestController
@RequestMapping("/unidades")
public class UnidadeController {

    private final UnidadeService unidadeService;

    public UnidadeController(UnidadeService unidadeService) {
        this.unidadeService = unidadeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GESTOR')")
    public UnidadeResponse criar(@Valid @RequestBody UnidadeRequest requisicao) {
        var unidade = unidadeService.criar(requisicao.nome(), requisicao.latitude(), requisicao.longitude());
        return UnidadeResponse.de(unidade);
    }

    @GetMapping("/{id}")
    public UnidadeResponse buscarPorId(@PathVariable Long id) {
        return UnidadeResponse.de(unidadeService.buscarPorId(id));
    }

    @GetMapping
    public List<UnidadeResponse> listar() {
        return unidadeService.listarTodas().stream().map(UnidadeResponse::de).toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public UnidadeResponse atualizar(@PathVariable Long id, @Valid @RequestBody UnidadeRequest requisicao) {
        var unidade = unidadeService.atualizar(id, requisicao.nome(), requisicao.latitude(), requisicao.longitude());
        return UnidadeResponse.de(unidade);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GESTOR')")
    public void excluir(@PathVariable Long id) {
        unidadeService.excluir(id);
    }
}
