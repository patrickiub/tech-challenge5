package br.com.fiap.vagazero.risco.infrastructure.persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoRiscoJpaRepository extends JpaRepository<AvaliacaoRiscoJpaEntity, Long> {

    List<AvaliacaoRiscoJpaEntity> findByAgendamentoIdOrderByAvaliadoEmDescIdDesc(Long agendamentoId);
}
