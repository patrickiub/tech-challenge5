package br.com.fiap.vagazero.identidade.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnZXN0b3JAdmFnYXplcm8uY29tIiwicGVyZmlsIjoiR0VTVE9SIn0.assinatura")
        String token) {
}
