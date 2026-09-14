package br.com.fiap.vagazero.fila.infrastructure.persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.fiap.vagazero.fila.domain.StatusAviso;

public interface AvisoPendenteJpaRepository extends JpaRepository<AvisoPendenteJpaEntity, Long> {

    List<AvisoPendenteJpaEntity> findByStatus(StatusAviso status);
}
