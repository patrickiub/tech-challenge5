package br.com.fiap.vagazero.risco.infrastructure.persistencia;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.risco.domain.ClassificacaoRisco;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "avaliacao_risco")
public class AvaliacaoRiscoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agendamento_id", nullable = false)
    private Long agendamentoId;

    @Column(nullable = false)
    private int score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassificacaoRisco classificacao;

    @Column(name = "avaliado_em", nullable = false)
    private LocalDateTime avaliadoEm;

    protected AvaliacaoRiscoJpaEntity() {
    }

    public AvaliacaoRiscoJpaEntity(
            Long id, Long agendamentoId, int score, ClassificacaoRisco classificacao, LocalDateTime avaliadoEm) {
        this.id = id;
        this.agendamentoId = agendamentoId;
        this.score = score;
        this.classificacao = classificacao;
        this.avaliadoEm = avaliadoEm;
    }

    public Long getId() {
        return id;
    }

    public Long getAgendamentoId() {
        return agendamentoId;
    }

    public int getScore() {
        return score;
    }

    public ClassificacaoRisco getClassificacao() {
        return classificacao;
    }

    public LocalDateTime getAvaliadoEm() {
        return avaliadoEm;
    }
}
