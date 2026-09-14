package br.com.fiap.vagazero.agenda.application;

/**
 * Porto usado pelo modulo demo para limpar agenda no reset. Deve ser
 * chamado por ultimo (depois de risco e fila), pois vaga e paciente sao
 * referenciados por convite, item_fila e avaliacao_risco.
 */
public interface LimparAgendaUseCase {

    /**
     * Remove agendamentos, vagas, unidades e pacientes, preservando o
     * paciente fixo (cnsPacienteFixo) criado pela migration V2 e vinculado
     * ao usuario de login PACIENTE de demonstracao.
     */
    void limparTudo(String cnsPacienteFixo);
}
