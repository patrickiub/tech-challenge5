package br.com.fiap.vagazero.fila.infrastructure.persistencia;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.fila.domain.StatusConvite;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "convite")
public class ConviteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vaga_id", nullable = false)
    private Long vagaId;

    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    @Column(name = "enviado_em", nullable = false)
    private LocalDateTime enviadoEm;

    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConvite status;

    @Column(name = "ordem_na_cascata", nullable = false)
    private Integer ordemNaCascata;

    protected ConviteJpaEntity() {
    }

    public ConviteJpaEntity(
            Long id, Long vagaId, Long pacienteId, LocalDateTime enviadoEm, LocalDateTime expiraEm,
            StatusConvite status, Integer ordemNaCascata) {
        this.id = id;
        this.vagaId = vagaId;
        this.pacienteId = pacienteId;
        this.enviadoEm = enviadoEm;
        this.expiraEm = expiraEm;
        this.status = status;
        this.ordemNaCascata = ordemNaCascata;
    }

    public Long getId() {
        return id;
    }

    public Long getVagaId() {
        return vagaId;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public LocalDateTime getEnviadoEm() {
        return enviadoEm;
    }

    public LocalDateTime getExpiraEm() {
        return expiraEm;
    }

    public StatusConvite getStatus() {
        return status;
    }

    public Integer getOrdemNaCascata() {
        return ordemNaCascata;
    }
}
