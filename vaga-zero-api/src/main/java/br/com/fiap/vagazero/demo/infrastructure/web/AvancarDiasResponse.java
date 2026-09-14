package br.com.fiap.vagazero.demo.infrastructure.web;

import java.time.LocalDateTime;

public record AvancarDiasResponse(int diasAvancados, LocalDateTime dataHoraAtualDaAplicacao) {
}
