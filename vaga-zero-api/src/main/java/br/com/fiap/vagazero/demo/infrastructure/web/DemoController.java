package br.com.fiap.vagazero.demo.infrastructure.web;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.demo.application.SeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Recursos de demonstracao: populam o cenario completo para a gravacao do
 * video e permitem avancar o relogio da aplicacao. Ativos apenas sob o
 * perfil Spring "demo" (ja ligado no compose.yml).
 */
@Tag(name = "9 - Demonstracao", description = "Endpoints de apoio a gravacao do video. Nao existem fora do "
        + "perfil 'demo'.")
@Profile("demo")
@RestController
@RequestMapping("/demo")
public class DemoController {

    private final SeedService seedService;

    public DemoController(SeedService seedService) {
        this.seedService = seedService;
    }

    @Operation(summary = "Popular o cenario de demonstracao",
            description = "Cria 1 unidade, 10 vagas de Oftalmologia e 30 pacientes na fila com perfis "
                    + "variados, mais dois pacientes com agendamento proprio para os dois caminhos de "
                    + "demonstracao (cascata manual e job D-2/D-1). A resposta traz os ids prontos e a "
                    + "instrucao de proximo passo de cada caminho. Chame uma unica vez por banco limpo - "
                    + "rodar de novo sem antes chamar /demo/reset falha por CNS duplicado.")
    @PostMapping("/seed")
    public SeedResultadoResponse seed() {
        return SeedResultadoResponse.de(seedService.semear());
    }

    @Operation(summary = "Avancar o relogio da aplicacao em N dias",
            description = "Afeta todo o sistema (o Clock e injetado em toda parte). Avancar o relogio expira "
                    + "qualquer convite ativo cujo TTL já tenha vencido em relacao ao novo instante - nao "
                    + "avance o relogio no meio de uma cascata que voce ainda quer aceitar manualmente.")
    @PostMapping("/avancar-dias/{n}")
    public AvancarDiasResponse avancarDias(@Parameter(example = "1") @PathVariable int n) {
        return new AvancarDiasResponse(n, seedService.avancarDias(n));
    }

    @Operation(summary = "Limpar e repopular o cenario de demonstracao",
            description = "Remove agendamentos, vagas, unidades, convites, fila de espera, avaliacoes de "
                    + "risco e pacientes (preservando o paciente/usuario fixo da migration), reseta o "
                    + "relogio para o instante real e roda o seed de novo. Use entre os dois caminhos de "
                    + "demonstracao para partir de um estado limpo.")
    @PostMapping("/reset")
    public SeedResultadoResponse reset() {
        return SeedResultadoResponse.de(seedService.resetar());
    }
}
