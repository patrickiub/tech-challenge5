package br.com.fiap.vagazero.shared.excecao;

/**
 * Base para excecoes de recurso inexistente, tratada genericamente como 404
 * pelo ApiExceptionHandler.
 */
public abstract class RecursoNaoEncontradoException extends VagaZeroException {

    protected RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
