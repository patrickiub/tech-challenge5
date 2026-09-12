package br.com.fiap.vagazero.shared.excecao;

/**
 * Base de toda excecao de negocio do sistema. Tratada centralmente pelo
 * ApiExceptionHandler, sem depender de framework.
 */
public abstract class VagaZeroException extends RuntimeException {

    protected VagaZeroException(String mensagem) {
        super(mensagem);
    }
}
