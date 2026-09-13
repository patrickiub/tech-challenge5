package br.com.fiap.vagazero.shared.web;

import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.fiap.vagazero.identidade.domain.CredenciaisInvalidasException;
import br.com.fiap.vagazero.shared.excecao.ConflitoException;
import br.com.fiap.vagazero.shared.excecao.RecursoNaoEncontradoException;
import br.com.fiap.vagazero.shared.excecao.VagaZeroException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResposta> tratar(CredenciaisInvalidasException excecao) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroResposta(excecao.getMessage()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratar(RecursoNaoEncontradoException excecao) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResposta(excecao.getMessage()));
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResposta> tratar(ConflitoException excecao) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResposta(excecao.getMessage()));
    }

    @ExceptionHandler(VagaZeroException.class)
    public ResponseEntity<ErroResposta> tratar(VagaZeroException excecao) {
        return ResponseEntity.badRequest().body(new ErroResposta(excecao.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratar(MethodArgumentNotValidException excecao) {
        // getAllErrors() inclui tanto erros de campo (@NotBlank, @Size, ...) quanto
        // erros de classe (validadores como @DadosClinicosCoerentesComPerfil), que
        // getFieldErrors() sozinho descartaria silenciosamente.
        String mensagem = excecao.getBindingResult().getAllErrors().stream()
                .map(erro -> erro instanceof FieldError fieldError
                        ? fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        : erro.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(new ErroResposta(mensagem));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> tratar(DataIntegrityViolationException excecao) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErroResposta(
                        "Nao foi possivel concluir a operacao: dado duplicado ou referencia a um "
                                + "registro inexistente"));
    }
}
