package br.com.fiap.vagazero.agenda.infrastructure.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoJpaRepository extends JpaRepository<AgendamentoJpaEntity, Long> {
}
