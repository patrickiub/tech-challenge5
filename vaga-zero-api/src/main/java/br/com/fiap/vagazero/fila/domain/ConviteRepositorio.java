package br.com.fiap.vagazero.fila.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConviteRepositorio {

    Convite salvar(Convite convite);

    Optional<Convite> buscarPorId(Long id);

    List<Convite> listarPorVaga(Long vagaId);

    List<Convite> listarEnviadosExpirados(LocalDateTime instante);

    /**
     * Transicao atomica: so aplica se o status em banco ainda for o esperado
     * (UPDATE condicional). Retorna true se a linha foi de fato alterada.
     * E a primitiva que impede aceite duplicado e a corrida entre aceite e
     * expiracao por TTL.
     */
    boolean atualizarStatusSeAtual(Long id, StatusConvite statusEsperado, StatusConvite statusNovo);
}
