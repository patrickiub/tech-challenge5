package br.com.fiap.vagazero.fila.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.fiap.vagazero.fila.application.CascataService;
import br.com.fiap.vagazero.shared.evento.VagaLiberadaEvento;
import br.com.fiap.vagazero.shared.kafka.KafkaTopics;

@Component
public class VagaLiberadaListener {

    private final CascataService cascataService;

    public VagaLiberadaListener(CascataService cascataService) {
        this.cascataService = cascataService;
    }

    /**
     * Kafka e at-least-once: o mesmo vaga.liberada pode chegar mais de uma vez
     * (retry de producer, rebalance de consumer, reprocessamento manual).
     * CascataService.iniciarCascata e idempotente por construcao (UPDATE
     * atomico DISPONIVEL -> EM_CASCATA), entao uma entrega duplicada aqui
     * simplesmente nao afeta nenhuma linha e nao inicia uma segunda cascata -
     * nao precisamos de deduplicacao propria do listener.
     */
    @KafkaListener(topics = KafkaTopics.VAGA_LIBERADA, groupId = "fila-cascata")
    public void ouvir(VagaLiberadaEvento evento) {
        cascataService.iniciarCascata(evento);
    }
}
