package br.com.fiap.vagazero.agenda.application;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.agenda.domain.Agendamento;

/**
 * Porto usado pelo modulo fila para criar o agendamento resultante do aceite
 * de um convite, sem acessar o repositorio de agenda diretamente.
 */
public interface AgendamentoCascataUseCase {

    Agendamento criarConfirmado(Long vagaId, Long pacienteId, LocalDateTime confirmadoEm);
}
