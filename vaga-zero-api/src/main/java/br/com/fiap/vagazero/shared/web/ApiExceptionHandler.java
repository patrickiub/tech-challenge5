package br.com.fiap.vagazero.shared.web;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.fiap.vagazero.identidade.domain.CredenciaisInvalidasException;
import br.com.fiap.vagazero.identidade.domain.EmailJaCadastradoException;
import br.com.fiap.vagazero.shared.excecao.VagaZeroException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResposta> tratar(CredenciaisInvalidasException excecao) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroResposta(excecao.getMessage()));
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErroResposta> tratar(EmailJaCadastradoException excecao) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResposta(excecao.getMessage()));
    }

    @ExceptionHandler(VagaZeroException.class)
    public ResponseEntity<ErroResposta> tratar(VagaZeroException excecao) {
        return ResponseEntity.badRequest().body(new ErroResposta(excecao.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratar(MethodArgumentNotValidException excecao) {
        String mensagem = excecao.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(new ErroResposta(mensagem));
    }
}
