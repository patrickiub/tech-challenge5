package br.com.fiap.vagazero.fila.infrastructure.web;

import java.time.Clock;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.fila.application.CascataService;

/**
 * Consulta de leitura do estado da cascata de uma vaga: ordem dos candidatos,
 * status de cada convite e tempo restante do convite ativo. Usado na
 * demonstracao em video.
 */
@RestController
@RequestMapping("/vagas/{vagaId}/cascata")
public class CascataController {

    private final CascataService cascataService;
    private final Clock clock;

    public CascataController(CascataService cascataService, Clock clock) {
        this.cascataService = cascataService;
        this.clock = clock;
    }

    @GetMapping
    public CascataEstadoResponse consultar(@PathVariable Long vagaId) {
        return CascataEstadoResponse.de(cascataService.consultarEstado(vagaId), clock);
    }
}
