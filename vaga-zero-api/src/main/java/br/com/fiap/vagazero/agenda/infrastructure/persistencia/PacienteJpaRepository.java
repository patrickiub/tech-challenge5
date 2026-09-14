package br.com.fiap.vagazero.agenda.infrastructure.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PacienteJpaRepository extends JpaRepository<PacienteJpaEntity, Long> {

    boolean existsByCns(String cns);

    @Transactional
    void deleteByCnsNot(String cns);
}
