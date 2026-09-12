package br.com.fiap.vagazero.agenda.infrastructure.persistencia;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.agenda.domain.StatusVaga;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vaga")
public class VagaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unidade_id", nullable = false)
    private Long unidadeId;

    @Column(nullable = false)
    private String especialidade;

    @Column(nullable = false)
    private String profissional;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusVaga status;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    protected VagaJpaEntity() {
    }

    public VagaJpaEntity(
            Long id, Long unidadeId, String especialidade, String profissional, LocalDateTime dataHora,
            StatusVaga status, LocalDateTime criadoEm) {
        this.id = id;
        this.unidadeId = unidadeId;
        this.especialidade = especialidade;
        this.profissional = profissional;
        this.dataHora = dataHora;
        this.status = status;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public String getProfissional() {
        return profissional;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public StatusVaga getStatus() {
        return status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
