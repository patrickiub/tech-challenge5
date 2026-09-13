package br.com.fiap.vagazero.fila.infrastructure.web;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.fila.application.CascataService;

@RestController
@RequestMapping("/convites")
public class ConviteController {

    private final CascataService cascataService;

    public ConviteController(CascataService cascataService) {
        this.cascataService = cascataService;
    }

    @PostMapping("/{id}/aceitar")
    public ResultadoAceiteResponse aceitar(@PathVariable Long id) {
        return ResultadoAceiteResponse.de(cascataService.aceitar(id));
    }

    @PostMapping("/{id}/recusar")
    public void recusar(@PathVariable Long id) {
        cascataService.recusar(id);
    }
}
