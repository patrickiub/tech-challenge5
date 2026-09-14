package br.com.fiap.vagazero.risco.infrastructure.persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoRiscoFatorJpaRepository extends JpaRepository<AvaliacaoRiscoFatorJpaEntity, Long> {

    List<AvaliacaoRiscoFatorJpaEntity> findByAvaliacaoRiscoId(Long avaliacaoRiscoId);
}
