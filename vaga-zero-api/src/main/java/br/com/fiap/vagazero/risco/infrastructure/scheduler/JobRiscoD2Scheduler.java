package br.com.fiap.vagazero.risco.infrastructure.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.fiap.vagazero.risco.application.AvaliacaoRiscoService;

/**
 * Job D-2/D-1: avalia risco dos agendamentos cuja vaga esta a ate 2 dias e
 * libera preventivamente a vaga de quem chegou em D-1 com classificacao
 * ALTO e sem confirmacao.
 */
@Component
public class JobRiscoD2Scheduler {

    private final AvaliacaoRiscoService avaliacaoRiscoService;

    public JobRiscoD2Scheduler(AvaliacaoRiscoService avaliacaoRiscoService) {
        this.avaliacaoRiscoService = avaliacaoRiscoService;
    }

    @Scheduled(fixedDelayString = "${vagazero.risco.scheduler-intervalo-ms}")
    public void processarAgendamentosFuturos() {
        avaliacaoRiscoService.processarAgendamentosFuturos();
    }
}
