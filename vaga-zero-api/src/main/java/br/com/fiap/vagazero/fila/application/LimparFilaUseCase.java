package br.com.fiap.vagazero.fila.application;

/**
 * Porto usado pelo modulo demo para limpar convites e fila de espera no
 * reset, sem acessar os repositorios de fila diretamente. Deve ser chamado
 * antes da limpeza de agenda (convite referencia vaga e paciente).
 */
public interface LimparFilaUseCase {

    void limparTudo();
}
