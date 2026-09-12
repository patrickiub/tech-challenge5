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

import br.com.fiap.vagazero.agenda.application.VagaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/vagas")
public class VagaController {

    private final VagaService vagaService;

    public VagaController(VagaService vagaService) {
        this.vagaService = vagaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('GESTOR')")
    public VagaResponse criar(@Valid @RequestBody VagaRequest requisicao) {
        var vaga = vagaService.criar(
                requisicao.unidadeId(), requisicao.especialidade(), requisicao.profissional(), requisicao.dataHora());
        return VagaResponse.de(vaga);
    }

    @GetMapping("/{id}")
    public VagaResponse buscarPorId(@PathVariable Long id) {
        return VagaResponse.de(vagaService.buscarPorId(id));
    }

    @GetMapping
    public List<VagaResponse> listar() {
        return vagaService.listarTodas().stream().map(VagaResponse::de).toList();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTOR')")
    public VagaResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarVagaRequest requisicao) {
        var vaga = vagaService.atualizar(
                id, requisicao.especialidade(), requisicao.profissional(), requisicao.dataHora(),
                requisicao.status());
        return VagaResponse.de(vaga);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('GESTOR')")
    public void excluir(@PathVariable Long id) {
        vagaService.excluir(id);
    }
}
