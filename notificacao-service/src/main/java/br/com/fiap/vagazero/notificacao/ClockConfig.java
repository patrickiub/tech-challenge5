package br.com.fiap.vagazero.notificacao;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Todo codigo deve obter a data/hora atual injetando java.time.Clock, nunca
 * LocalDateTime.now() direto - mesma regra do vaga-zero-api.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of("America/Sao_Paulo"));
    }
}
