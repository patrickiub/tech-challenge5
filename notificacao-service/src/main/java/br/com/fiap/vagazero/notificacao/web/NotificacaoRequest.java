package br.com.fiap.vagazero.notificacao.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificacaoRequest(
        @NotBlank String destinatario,
        @NotBlank String canal,
        @NotBlank String mensagem,
        @NotNull Long conviteId) {
}
