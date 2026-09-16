package br.com.fiap.vagazero.agenda.application;

/**
 * Porto usado pelo modulo demo para remover, no reset, os pacientes que nao
 * sao o fixo de demonstracao. As demais tabelas (agendamento, vaga, unidade
 * e todo o resto) sao limpas antes disso por um TRUNCATE generico
 * (TruncadorDeTabelas) - paciente fica de fora dele porque uma linha
 * precisa sobreviver, entao a limpeza dela e sempre a exclusao seletiva
 * feita aqui.
 */
public interface LimparAgendaUseCase {

    /**
     * Remove todos os pacientes exceto o fixo (cnsPacienteFixo) criado pela
     * migration V2 e vinculado ao usuario de login PACIENTE de demonstracao.
     */
    void limparTudo(String cnsPacienteFixo);
}
