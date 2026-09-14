package br.com.fiap.vagazero.notificacao.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ChaosAtivoException extends RuntimeException {

    public ChaosAtivoException() {
        super("Modo chaos ligado: falha proposital para demonstrar resiliencia");
    }
}
