package br.com.fiap.vagazero.risco.infrastructure.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.risco.application.AvaliacaoRiscoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "5 - Risco de falta", description = "Motor de scoring deterministico e explicavel: cada avaliacao "
        + "detalha os fatores que compuseram o score.")
@RestController
@RequestMapping("/agendamentos/{agendamentoId}/risco")
public class RiscoController {

    private final AvaliacaoRiscoService avaliacaoRiscoService;

    public RiscoController(AvaliacaoRiscoService avaliacaoRiscoService) {
        this.avaliacaoRiscoService = avaliacaoRiscoService;
    }

    @Operation(summary = "Consultar avaliacao de risco de falta de um agendamento",
            description = "Recalcula e persiste uma nova avaliacao a cada chamada, refletindo o estado atual "
                    + "do agendamento (por exemplo, uma confirmacao de presenca recente ja aparece refletida "
                    + "no score). A resposta detalha score, classificacao (BAIXO/MEDIO/ALTO) e a lista de "
                    + "fatores que compuseram o score - a explicabilidade do motor de regras.")
    @ApiResponse(responseCode = "200", description = "Avaliacao calculada")
    @ApiResponse(responseCode = "404", description = "Agendamento nao encontrado")
    @GetMapping
    public AvaliacaoRiscoResponse avaliar(@Parameter(example = "1") @PathVariable Long agendamentoId) {
        return AvaliacaoRiscoResponse.de(avaliacaoRiscoService.avaliar(agendamentoId));
    }
}
