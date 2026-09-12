package br.com.fiap.vagazero.agenda.infrastructure.persistencia;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "agendamento")
public class AgendamentoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vaga_id", nullable = false)
    private Long vagaId;

    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;

    @Column(name = "confirmado_em")
    private LocalDateTime confirmadoEm;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    protected AgendamentoJpaEntity() {
    }

    public AgendamentoJpaEntity(
            Long id, Long vagaId, Long pacienteId, StatusAgendamento status, LocalDateTime confirmadoEm,
            LocalDateTime criadoEm) {
        this.id = id;
        this.vagaId = vagaId;
        this.pacienteId = pacienteId;
        this.status = status;
        this.confirmadoEm = confirmadoEm;
        this.criadoEm = criadoEm;
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

    public StatusAgendamento getStatus() {
        return status;
    }

    public LocalDateTime getConfirmadoEm() {
        return confirmadoEm;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
