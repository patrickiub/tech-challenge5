package br.com.fiap.vagazero.fila.infrastructure.persistencia;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemFilaJpaRepository extends JpaRepository<ItemFilaJpaEntity, Long> {

    List<ItemFilaJpaEntity> findByEspecialidade(String especialidade);

    Optional<ItemFilaJpaEntity> findByPacienteIdAndEspecialidade(Long pacienteId, String especialidade);
}
