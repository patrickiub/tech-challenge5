package br.com.fiap.vagazero.fila.infrastructure.web;

import java.time.Clock;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.fila.application.CascataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Consulta de leitura do estado da cascata de uma vaga: ordem dos candidatos,
 * status de cada convite e tempo restante do convite ativo. Usado na
 * demonstracao em video.
 */
@Tag(name = "4 - Cascata de vagas")
@RestController
@RequestMapping("/vagas/{vagaId}/cascata")
public class CascataController {

    private final CascataService cascataService;
    private final Clock clock;

    public CascataController(CascataService cascataService, Clock clock) {
        this.cascataService = cascataService;
        this.clock = clock;
    }

    @Operation(summary = "Consultar estado da cascata de uma vaga",
            description = "Mostra a ordem completa dos candidatos elegiveis, o status do convite de cada "
                    + "um (ou AGUARDANDO_VEZ, se ainda nao foi chamado) e o tempo restante do convite "
                    + "ativo. Execute apos cancelar um agendamento para acompanhar a cascata em tempo real.")
    @ApiResponse(responseCode = "404", description = "Vaga nao encontrada")
    @GetMapping
    public CascataEstadoResponse consultar(@Parameter(example = "1") @PathVariable Long vagaId) {
        return CascataEstadoResponse.de(cascataService.consultarEstado(vagaId), clock);
    }
}
