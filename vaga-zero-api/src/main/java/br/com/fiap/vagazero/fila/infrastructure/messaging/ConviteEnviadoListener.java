package br.com.fiap.vagazero.fila.infrastructure.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.fiap.vagazero.agenda.application.ConsultaPacienteUseCase;
import br.com.fiap.vagazero.agenda.application.PacienteResumo;
import br.com.fiap.vagazero.fila.infrastructure.notificacao.AvisoNotificacao;
import br.com.fiap.vagazero.fila.infrastructure.notificacao.NotificacaoClient;
import br.com.fiap.vagazero.shared.evento.ConviteEnviadoEvento;
import br.com.fiap.vagazero.shared.kafka.KafkaTopics;

/**
 * Reage a convite.enviado avisando o candidato via notificacao-service. E
 * um listener deliberadamente separado do motor da cascata: falha aqui
 * (mesmo apos timeout/retry/circuit breaker) nunca pode afetar a resposta
 * HTTP de quem cancelou o agendamento ou recusou o convite anterior - o
 * convite ja esta persistido como ENVIADO antes deste listener rodar.
 */
@Component
public class ConviteEnviadoListener {

    private static final String CANAL = "SMS";

    private final NotificacaoClient notificacaoClient;
    private final ConsultaPacienteUseCase consultaPacienteUseCase;

    public ConviteEnviadoListener(NotificacaoClient notificacaoClient, ConsultaPacienteUseCase consultaPacienteUseCase) {
        this.notificacaoClient = notificacaoClient;
        this.consultaPacienteUseCase = consultaPacienteUseCase;
    }

    @KafkaListener(topics = KafkaTopics.CONVITE_ENVIADO, groupId = "fila-notificacao")
    public void ouvir(ConviteEnviadoEvento evento) {
        PacienteResumo paciente = consultaPacienteUseCase.buscarResumo(evento.pacienteId());
        String mensagem = "Vaga disponivel! Voce tem ate " + evento.expiraEm() + " para confirmar.";
        notificacaoClient.enviar(new AvisoNotificacao(paciente.nome(), CANAL, mensagem, evento.conviteId()));
    }
}
