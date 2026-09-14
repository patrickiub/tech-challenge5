package br.com.fiap.vagazero.agenda.infrastructure.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteJpaRepository extends JpaRepository<PacienteJpaEntity, Long> {

    boolean existsByCns(String cns);

    void deleteByCnsNot(String cns);
}
