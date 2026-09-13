package br.com.fiap.vagazero.shared.web;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String ESQUEMA_JWT = "bearerAuth";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vaga Zero API")
                        .description("""
                                Recuperacao de vagas ociosas no SUS - Hackathon FIAP Fase 5.

                                Como testar (uma unica vez):
                                1. Abra "1 - Autenticacao" > POST /auth/login e clique em Try it out, Execute \
                                (o corpo ja vem preenchido com o usuario gestor de demonstracao).
                                2. Copie o valor do campo "token" da resposta.
                                3. Clique no botao Authorize no topo desta pagina e cole o token no campo \
                                (Bearer <token>).
                                4. A partir dai, todos os endpoints protegidos podem ser executados direto: \
                                cada corpo de requisicao ja vem preenchido com dados coerentes entre si. \
                                Siga as secoes numeradas de cima para baixo (2 - Cadastros, 3 - Fila de \
                                espera, 4 - Cascata de vagas) para reproduzir a demonstracao completa: \
                                cadastrar unidade/vaga, colocar um paciente na fila, cancelar o agendamento \
                                e acompanhar a cascata de convites ate o aceite.
                                """)
                        .version("0.1.0"))
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_JWT))
                .components(new Components()
                        .addSecuritySchemes(ESQUEMA_JWT, new SecurityScheme()
                                .name(ESQUEMA_JWT)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
