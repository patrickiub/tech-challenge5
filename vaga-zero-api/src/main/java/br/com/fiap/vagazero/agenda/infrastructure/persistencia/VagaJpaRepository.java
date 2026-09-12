package br.com.fiap.vagazero.agenda.infrastructure.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VagaJpaRepository extends JpaRepository<VagaJpaEntity, Long> {
}
