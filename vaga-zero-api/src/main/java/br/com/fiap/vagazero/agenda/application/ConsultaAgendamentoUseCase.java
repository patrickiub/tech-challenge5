package br.com.fiap.vagazero.agenda.application;

import java.util.List;

import br.com.fiap.vagazero.agenda.domain.Agendamento;
import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;

/**
 * Porto usado pelo modulo risco para ler o historico de agendamentos de um
 * paciente (faltas, primeira consulta na especialidade) e para varrer os
 * agendamentos futuros no job de risco D-2, sem acessar o repositorio de
 * agenda diretamente. cancelar reusa o mesmo fluxo do cancelamento manual
 * (publica agendamento.cancelado e vaga.liberada) para a liberacao
 * preventiva de vaga com risco ALTO.
 */
public interface ConsultaAgendamentoUseCase {

    Agendamento buscarPorId(Long id);

    List<Agendamento> listarPorPaciente(Long pacienteId);

    List<Agendamento> listarPorStatus(StatusAgendamento status);

    Agendamento cancelar(Long id);
}
