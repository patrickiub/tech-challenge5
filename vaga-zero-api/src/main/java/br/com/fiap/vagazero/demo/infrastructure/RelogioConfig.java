package br.com.fiap.vagazero.demo.infrastructure;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Expoe o Clock da aplicacao. Todo codigo deve obter a data/hora atual injetando
 * java.time.Clock (nunca LocalDateTime.now() direto), para que o avanco de tempo
 * dos recursos de demonstracao afete o sistema inteiro.
 */
@Configuration
public class RelogioConfig {

    @Bean
    public RelogioAjustavel relogioAjustavel() {
        return new RelogioAjustavel(ZoneId.of("America/Sao_Paulo"));
    }

    @Bean
    public Clock clock(RelogioAjustavel relogioAjustavel) {
        return relogioAjustavel;
    }
}
