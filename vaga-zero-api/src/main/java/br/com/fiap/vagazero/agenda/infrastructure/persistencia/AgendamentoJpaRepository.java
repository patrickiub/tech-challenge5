package br.com.fiap.vagazero.agenda.infrastructure.persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;

public interface AgendamentoJpaRepository extends JpaRepository<AgendamentoJpaEntity, Long> {

    List<AgendamentoJpaEntity> findByPacienteId(Long pacienteId);

    List<AgendamentoJpaEntity> findByStatus(StatusAgendamento status);
}
