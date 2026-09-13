package br.com.fiap.vagazero.shared.web;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

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

    private final Clock clock;

    public ApiExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<ErroResposta> tratar(CredenciaisInvalidasException excecao) {
        return responder(HttpStatus.UNAUTHORIZED, excecao,
                excecao.getMessage() + ". Confira o email e a senha e tente novamente.");
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratar(RecursoNaoEncontradoException excecao) {
        return responder(HttpStatus.NOT_FOUND, excecao,
                excecao.getMessage() + ". Verifique o id informado e tente novamente.");
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResposta> tratar(ConflitoException excecao) {
        return responder(HttpStatus.CONFLICT, excecao,
                excecao.getMessage() + ". Ajuste a requisicao (outro id/valor) e tente novamente.");
    }

    @ExceptionHandler(VagaZeroException.class)
    public ResponseEntity<ErroResposta> tratar(VagaZeroException excecao) {
        return responder(HttpStatus.BAD_REQUEST, excecao,
                excecao.getMessage() + ". Corrija a requisicao e tente novamente.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratar(MethodArgumentNotValidException excecao) {
        // getAllErrors() inclui tanto erros de campo (@NotBlank, @Size, ...) quanto
        // erros de classe (validadores como @DadosClinicosCoerentesComPerfil), que
        // getFieldErrors() sozinho descartaria silenciosamente.
        List<ErroCampo> erros = excecao.getBindingResult().getAllErrors().stream()
                .map(erro -> erro instanceof FieldError fieldError
                        ? new ErroCampo(fieldError.getField(), fieldError.getDefaultMessage())
                        : new ErroCampo("geral", erro.getDefaultMessage()))
                .toList();
        ErroResposta corpo = new ErroResposta(
                LocalDateTime.now(clock), HttpStatus.BAD_REQUEST.value(), "VALIDACAO",
                "Dados invalidos. Corrija os campos indicados abaixo e tente novamente.", erros);
        return ResponseEntity.badRequest().body(corpo);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> tratar(DataIntegrityViolationException excecao) {
        ErroResposta corpo = new ErroResposta(
                LocalDateTime.now(clock), HttpStatus.CONFLICT.value(), "DADO_DUPLICADO_OU_INVALIDO",
                "Nao foi possivel concluir a operacao: dado duplicado ou referencia a um registro "
                        + "inexistente. Verifique os valores unicos (email, cns) e os ids referenciados.");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corpo);
    }

    private ResponseEntity<ErroResposta> responder(HttpStatus status, Exception excecao, String mensagem) {
        String codigo = codigoDe(excecao);
        ErroResposta corpo = new ErroResposta(LocalDateTime.now(clock), status.value(), codigo, mensagem);
        return ResponseEntity.status(status).body(corpo);
    }

    /**
     * Deriva um codigo de erro estavel (ex.: VAGA_NAO_ENCONTRADA) a partir do
     * nome da classe da excecao, sem exigir manutencao manual por tipo.
     */
    private String codigoDe(Exception excecao) {
        String nome = excecao.getClass().getSimpleName().replace("Exception", "");
        return nome.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }
}
