package br.com.fiap.vagazero.risco.application;

/**
 * Porto usado pelo modulo demo para limpar as avaliacoes de risco no reset,
 * sem acessar o repositorio de risco diretamente. Deve ser chamado antes da
 * limpeza de agenda (avaliacao_risco referencia agendamento).
 */
public interface LimparRiscoUseCase {

    void limparTudo();
}
