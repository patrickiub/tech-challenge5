package br.com.fiap.vagazero.fila.infrastructure.persistencia;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.fiap.vagazero.fila.domain.StatusConvite;

public interface ConviteJpaRepository extends JpaRepository<ConviteJpaEntity, Long> {

    List<ConviteJpaEntity> findByVagaIdOrderByOrdemNaCascataAsc(Long vagaId);

    List<ConviteJpaEntity> findByStatusAndExpiraEmBefore(StatusConvite status, LocalDateTime instante);

    @Modifying
    @Query("update ConviteJpaEntity c set c.status = :statusNovo "
            + "where c.id = :id and c.status = :statusEsperado")
    int atualizarStatusSeAtual(
            @Param("id") Long id,
            @Param("statusEsperado") StatusConvite statusEsperado,
            @Param("statusNovo") StatusConvite statusNovo);
}
