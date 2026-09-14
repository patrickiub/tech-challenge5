package br.com.fiap.vagazero.fila.infrastructure.web;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.fila.domain.AvisoPendente;
import io.swagger.v3.oas.annotations.media.Schema;

public record AvisoPendenteResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "1") Long conviteId,
        @Schema(example = "Ana Beatriz Ramos") String destinatario,
        @Schema(example = "SMS") String canal,
        @Schema(example = "Vaga disponivel! Voce tem ate 2026-09-14T10:30:00 para confirmar.") String mensagem,
        @Schema(example = "PENDENTE") String status,
        @Schema(example = "2") int tentativas,
        @Schema(example = "2026-09-14T10:00:05") LocalDateTime criadoEm,
        @Schema(example = "2026-09-14T10:00:07") LocalDateTime ultimaTentativaEm) {

    public static AvisoPendenteResponse de(AvisoPendente aviso) {
        return new AvisoPendenteResponse(
                aviso.id(), aviso.conviteId(), aviso.destinatario(), aviso.canal(), aviso.mensagem(),
                aviso.status().name(), aviso.tentativas(), aviso.criadoEm(), aviso.ultimaTentativaEm());
    }
}
