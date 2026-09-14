package br.com.fiap.vagazero.fila.infrastructure.persistencia;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.fila.domain.StatusAviso;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aviso_pendente")
public class AvisoPendenteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "convite_id", nullable = false)
    private Long conviteId;

    @Column(nullable = false)
    private String destinatario;

    @Column(nullable = false)
    private String canal;

    @Column(nullable = false)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAviso status;

    @Column(nullable = false)
    private int tentativas;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "ultima_tentativa_em", nullable = false)
    private LocalDateTime ultimaTentativaEm;

    protected AvisoPendenteJpaEntity() {
    }

    public AvisoPendenteJpaEntity(
            Long id, Long conviteId, String destinatario, String canal, String mensagem, StatusAviso status,
            int tentativas, LocalDateTime criadoEm, LocalDateTime ultimaTentativaEm) {
        this.id = id;
        this.conviteId = conviteId;
        this.destinatario = destinatario;
        this.canal = canal;
        this.mensagem = mensagem;
        this.status = status;
        this.tentativas = tentativas;
        this.criadoEm = criadoEm;
        this.ultimaTentativaEm = ultimaTentativaEm;
    }

    public Long getId() {
        return id;
    }

    public Long getConviteId() {
        return conviteId;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getCanal() {
        return canal;
    }

    public String getMensagem() {
        return mensagem;
    }

    public StatusAviso getStatus() {
        return status;
    }

    public int getTentativas() {
        return tentativas;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getUltimaTentativaEm() {
        return ultimaTentativaEm;
    }
}
