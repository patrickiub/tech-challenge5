package br.com.fiap.vagazero.agenda.infrastructure.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.vagazero.agenda.domain.StatusVaga;

public interface VagaJpaRepository extends JpaRepository<VagaJpaEntity, Long> {

    @Transactional
    @Modifying
    @Query("update VagaJpaEntity v set v.status = :statusNovo "
            + "where v.id = :id and v.status = :statusEsperado")
    int mudarStatusSeAtual(
            @Param("id") Long id,
            @Param("statusEsperado") StatusVaga statusEsperado,
            @Param("statusNovo") StatusVaga statusNovo);
}
