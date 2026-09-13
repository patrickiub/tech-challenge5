package br.com.fiap.vagazero.shared.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Declara os topicos como beans NewTopic para que o KafkaAdmin do Spring os
 * crie automaticamente na subida da aplicacao, independente da configuracao
 * de auto-create do broker.
 */
@Configuration
public class KafkaTopicsConfig {

    @Bean
    public NewTopic agendamentoCancelado() {
        return TopicBuilder.name(KafkaTopics.AGENDAMENTO_CANCELADO).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic vagaLiberada() {
        return TopicBuilder.name(KafkaTopics.VAGA_LIBERADA).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic riscoAvaliado() {
        return TopicBuilder.name(KafkaTopics.RISCO_AVALIADO).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic conviteEnviado() {
        return TopicBuilder.name(KafkaTopics.CONVITE_ENVIADO).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic conviteAceito() {
        return TopicBuilder.name(KafkaTopics.CONVITE_ACEITO).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic conviteExpirado() {
        return TopicBuilder.name(KafkaTopics.CONVITE_EXPIRADO).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic vagaPreenchida() {
        return TopicBuilder.name(KafkaTopics.VAGA_PREENCHIDA).partitions(1).replicas(1).build();
    }
}
