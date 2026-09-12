package br.com.fiap.vagazero.agenda.infrastructure.persistencia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "paciente")
public class PacienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cns;

    private String telefone;

    @Column(nullable = false)
    private BigDecimal latitude;

    @Column(nullable = false)
    private BigDecimal longitude;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    protected PacienteJpaEntity() {
    }

    public PacienteJpaEntity(
            Long id, String nome, String cns, String telefone, BigDecimal latitude, BigDecimal longitude,
            LocalDate dataNascimento, LocalDateTime criadoEm) {
        this.id = id;
        this.nome = nome;
        this.cns = cns;
        this.telefone = telefone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dataNascimento = dataNascimento;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCns() {
        return cns;
    }

    public String getTelefone() {
        return telefone;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
