package br.com.fiap.vagazero.fila.infrastructure.notificacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import br.com.fiap.vagazero.fila.domain.AvisoPendente;
import br.com.fiap.vagazero.fila.domain.AvisoPendenteRepositorio;
import br.com.fiap.vagazero.fila.domain.StatusAviso;

/**
 * O fallback e a garantia central de resiliencia: mesmo com o
 * notificacao-service fora, nenhum aviso pode se perder. Estes testes
 * chamam os metodos de fallback diretamente (sem contexto Spring/AOP -
 * @Retry/@CircuitBreaker so agem via proxy, entao aqui testamos a logica
 * que o fallback executa quando e acionado).
 */
@ExtendWith(MockitoExtension.class)
class NotificacaoClientTest {

    @Mock
    private AvisoPendenteRepositorio avisoPendenteRepositorio;

    private final Clock clock = Clock.fixed(Instant.parse("2026-09-14T10:00:00Z"), ZoneOffset.UTC);
    private final RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);

    private NotificacaoClient criarCliente() {
        return new NotificacaoClient(restClient, avisoPendenteRepositorio, clock);
    }

    @Test
    void enviarFallback_enfileiraNovoAvisoPendenteComUmaTentativa() {
        NotificacaoClient client = criarCliente();
        AvisoNotificacao aviso = new AvisoNotificacao("Ana Beatriz Ramos", "SMS", "Vaga disponivel!", 10L);

        client.enviarFallback(aviso, new RuntimeException("notificacao-service indisponivel"));

        ArgumentCaptor<AvisoPendente> captor = ArgumentCaptor.forClass(AvisoPendente.class);
        verify(avisoPendenteRepositorio).salvar(captor.capture());
        AvisoPendente pendente = captor.getValue();
        assertThat(pendente.id()).isNull();
        assertThat(pendente.conviteId()).isEqualTo(10L);
        assertThat(pendente.destinatario()).isEqualTo("Ana Beatriz Ramos");
        assertThat(pendente.canal()).isEqualTo("SMS");
        assertThat(pendente.mensagem()).isEqualTo("Vaga disponivel!");
        assertThat(pendente.status()).isEqualTo(StatusAviso.PENDENTE);
        assertThat(pendente.tentativas()).isEqualTo(1);
        assertThat(pendente.criadoEm()).isEqualTo(LocalDateTime.now(clock));
        assertThat(pendente.ultimaTentativaEm()).isEqualTo(LocalDateTime.now(clock));
    }

    @Test
    void reenviarFallback_incrementaTentativasEMantemPendente() {
        NotificacaoClient client = criarCliente();
        LocalDateTime criadoEm = LocalDateTime.now(clock).minusMinutes(10);
        AvisoPendente pendenteExistente = new AvisoPendente(
                99L, 10L, "Ana Beatriz Ramos", "SMS", "Vaga disponivel!", StatusAviso.PENDENTE, 2, criadoEm,
                criadoEm.plusMinutes(3));

        client.reenviarFallback(pendenteExistente, new RuntimeException("circuito aberto"));

        ArgumentCaptor<AvisoPendente> captor = ArgumentCaptor.forClass(AvisoPendente.class);
        verify(avisoPendenteRepositorio).salvar(captor.capture());
        AvisoPendente atualizado = captor.getValue();
        assertThat(atualizado.id()).isEqualTo(99L);
        assertThat(atualizado.status()).isEqualTo(StatusAviso.PENDENTE);
        assertThat(atualizado.tentativas()).isEqualTo(3);
        assertThat(atualizado.criadoEm()).isEqualTo(criadoEm);
        assertThat(atualizado.ultimaTentativaEm()).isEqualTo(LocalDateTime.now(clock));
    }

    @Test
    void reenviar_comSucesso_marcaAvisoComoEnviado() {
        when(restClient.post().uri(any(String.class)).contentType(any()).body(any()).retrieve().toBodilessEntity())
                .thenReturn(null);
        NotificacaoClient client = criarCliente();
        LocalDateTime criadoEm = LocalDateTime.now(clock).minusMinutes(10);
        AvisoPendente pendente = new AvisoPendente(
                5L, 20L, "Roberto Nunes", "SMS", "Vaga disponivel!", StatusAviso.PENDENTE, 1, criadoEm,
                criadoEm.plusMinutes(1));

        client.reenviar(pendente);

        ArgumentCaptor<AvisoPendente> captor = ArgumentCaptor.forClass(AvisoPendente.class);
        verify(avisoPendenteRepositorio).salvar(captor.capture());
        AvisoPendente enviado = captor.getValue();
        assertThat(enviado.id()).isEqualTo(5L);
        assertThat(enviado.status()).isEqualTo(StatusAviso.ENVIADO);
        assertThat(enviado.tentativas()).isEqualTo(2);
        assertThat(enviado.ultimaTentativaEm()).isEqualTo(LocalDateTime.now(clock));
    }
}
