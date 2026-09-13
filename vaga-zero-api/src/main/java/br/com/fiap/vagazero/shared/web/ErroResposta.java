package br.com.fiap.vagazero.shared.web;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Formato unico de erro da API: timestamp, status HTTP, um codigo estavel
 * para automacao (derivado do tipo da excecao) e uma mensagem em portugues
 * que diz o que fazer, nao so o que falhou. erros vem preenchido apenas
 * para falhas de validacao (um item por campo invalido).
 */
public record ErroResposta(
        @Schema(example = "2026-09-13T14:32:10.123") LocalDateTime timestamp,
        @Schema(example = "404") int status,
        @Schema(example = "VAGA_NAO_ENCONTRADA") String codigo,
        @Schema(example = "Vaga nao encontrada: 99. Verifique o id e tente novamente.") String mensagem,
        List<ErroCampo> erros) {

    public ErroResposta(LocalDateTime timestamp, int status, String codigo, String mensagem) {
        this(timestamp, status, codigo, mensagem, List.of());
    }
}
