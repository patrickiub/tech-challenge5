package br.com.fiap.vagazero.notificacao.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ChaosRequestInvalidoException extends RuntimeException {

    public ChaosRequestInvalidoException(String estado) {
        super("Estado invalido: " + estado + ". Use 'on' ou 'off'.");
    }
}
