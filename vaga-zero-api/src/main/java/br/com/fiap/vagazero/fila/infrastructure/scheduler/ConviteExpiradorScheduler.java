package br.com.fiap.vagazero.fila.infrastructure.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.fiap.vagazero.fila.application.CascataService;

@Component
public class ConviteExpiradorScheduler {

    private final CascataService cascataService;

    public ConviteExpiradorScheduler(CascataService cascataService) {
        this.cascataService = cascataService;
    }

    @Scheduled(fixedDelayString = "${vagazero.convite.scheduler-intervalo-ms}")
    public void expirarConvitesVencidos() {
        cascataService.expirarVencidos();
    }
}
