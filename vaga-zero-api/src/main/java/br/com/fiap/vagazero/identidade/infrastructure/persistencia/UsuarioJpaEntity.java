package br.com.fiap.vagazero.identidade.infrastructure.persistencia;

import java.time.LocalDateTime;

import br.com.fiap.vagazero.identidade.domain.Perfil;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class UsuarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    @Column(name = "paciente_id")
    private Long pacienteId;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    protected UsuarioJpaEntity() {
    }

    public UsuarioJpaEntity(Long id, String email, String senhaHash, Perfil perfil, Long pacienteId, LocalDateTime criadoEm) {
        this.id = id;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.pacienteId = pacienteId;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
