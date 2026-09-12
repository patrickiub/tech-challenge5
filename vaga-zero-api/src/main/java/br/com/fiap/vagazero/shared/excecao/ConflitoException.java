package br.com.fiap.vagazero.shared.excecao;

/**
 * Base para excecoes de conflito (duplicidade, estado incompativel), tratada
 * genericamente como 409 pelo ApiExceptionHandler.
 */
public abstract class ConflitoException extends VagaZeroException {

    protected ConflitoException(String mensagem) {
        super(mensagem);
    }
}
