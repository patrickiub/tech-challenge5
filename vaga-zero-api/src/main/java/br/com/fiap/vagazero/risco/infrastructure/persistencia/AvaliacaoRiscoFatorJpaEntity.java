package br.com.fiap.vagazero.risco.infrastructure.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "avaliacao_risco_fator")
public class AvaliacaoRiscoFatorJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "avaliacao_risco_id", nullable = false)
    private Long avaliacaoRiscoId;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private int pontos;

    protected AvaliacaoRiscoFatorJpaEntity() {
    }

    public AvaliacaoRiscoFatorJpaEntity(Long id, Long avaliacaoRiscoId, String codigo, String descricao, int pontos) {
        this.id = id;
        this.avaliacaoRiscoId = avaliacaoRiscoId;
        this.codigo = codigo;
        this.descricao = descricao;
        this.pontos = pontos;
    }

    public Long getId() {
        return id;
    }

    public Long getAvaliacaoRiscoId() {
        return avaliacaoRiscoId;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPontos() {
        return pontos;
    }
}
