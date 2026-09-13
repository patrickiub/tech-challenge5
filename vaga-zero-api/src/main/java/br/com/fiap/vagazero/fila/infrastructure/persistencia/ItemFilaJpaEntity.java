package br.com.fiap.vagazero.fila.infrastructure.persistencia;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "item_fila")
public class ItemFilaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    @Column(nullable = false)
    private String especialidade;

    @Column(name = "data_entrada", nullable = false)
    private LocalDateTime dataEntrada;

    @Column(name = "prioridade_clinica", nullable = false)
    private Short prioridadeClinica;

    @Column(name = "aceita_chamado_imediato", nullable = false)
    private boolean aceitaChamadoImediato;

    @Column(name = "raio_maximo_km", nullable = false)
    private BigDecimal raioMaximoKm;

    protected ItemFilaJpaEntity() {
    }

    public ItemFilaJpaEntity(
            Long id, Long pacienteId, String especialidade, LocalDateTime dataEntrada, Short prioridadeClinica,
            boolean aceitaChamadoImediato, BigDecimal raioMaximoKm) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.especialidade = especialidade;
        this.dataEntrada = dataEntrada;
        this.prioridadeClinica = prioridadeClinica;
        this.aceitaChamadoImediato = aceitaChamadoImediato;
        this.raioMaximoKm = raioMaximoKm;
    }

    public Long getId() {
        return id;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public Short getPrioridadeClinica() {
        return prioridadeClinica;
    }

    public boolean isAceitaChamadoImediato() {
        return aceitaChamadoImediato;
    }

    public BigDecimal getRaioMaximoKm() {
        return raioMaximoKm;
    }
}
