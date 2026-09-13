package br.com.fiap.vagazero.shared.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventoPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventoPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicar(String topico, String chave, Object evento) {
        kafkaTemplate.send(topico, chave, evento);
    }
}
