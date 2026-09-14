package br.com.fiap.vagazero.fila.infrastructure.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.fila.domain.AvisoPendenteRepositorio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Lista os avisos de convite que caram no fallback do Resilience4j e ainda
 * nao foram reenviados com sucesso - torna o reprocessamento visivel na
 * tela: a lista cresce enquanto o circuito esta aberto e esvazia sozinha
 * depois que o notificacao-service volta.
 */
@Tag(name = "6 - Resiliencia")
@RestController
@RequestMapping("/admin/avisos-pendentes")
public class AvisoPendenteController {

    private final AvisoPendenteRepositorio avisoPendenteRepositorio;

    public AvisoPendenteController(AvisoPendenteRepositorio avisoPendenteRepositorio) {
        this.avisoPendenteRepositorio = avisoPendenteRepositorio;
    }

    @Operation(summary = "Listar avisos de notificacao pendentes de reenvio",
            description = "So os avisos com status PENDENTE (quem ja foi reenviado com sucesso vira ENVIADO e "
                    + "some desta lista). Cada tentativa de reenvio (bem ou mal sucedida) atualiza tentativas "
                    + "e ultimaTentativaEm.")
    @GetMapping
    public List<AvisoPendenteResponse> listar() {
        return avisoPendenteRepositorio.listarPendentes().stream().map(AvisoPendenteResponse::de).toList();
    }
}
