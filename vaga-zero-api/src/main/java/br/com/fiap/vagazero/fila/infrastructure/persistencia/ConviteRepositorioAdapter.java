package br.com.fiap.vagazero.fila.infrastructure.persistencia;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.fiap.vagazero.fila.domain.Convite;
import br.com.fiap.vagazero.fila.domain.ConviteRepositorio;
import br.com.fiap.vagazero.fila.domain.StatusConvite;

@Repository
public class ConviteRepositorioAdapter implements ConviteRepositorio {

    private final ConviteJpaRepository jpaRepository;

    public ConviteRepositorioAdapter(ConviteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Convite salvar(Convite convite) {
        ConviteJpaEntity entidade = new ConviteJpaEntity(
                convite.id(), convite.vagaId(), convite.pacienteId(), convite.enviadoEm(), convite.expiraEm(),
                convite.status(), convite.ordemNaCascata());
        return paraDomain(jpaRepository.save(entidade));
    }

    @Override
    public Optional<Convite> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::paraDomain);
    }

    @Override
    public List<Convite> listarPorVaga(Long vagaId) {
        return jpaRepository.findByVagaIdOrderByOrdemNaCascataAsc(vagaId).stream().map(this::paraDomain).toList();
    }

    @Override
    public List<Convite> listarEnviadosExpirados(LocalDateTime instante) {
        return jpaRepository.findByStatusAndExpiraEmBefore(StatusConvite.ENVIADO, instante).stream()
                .map(this::paraDomain)
                .toList();
    }

    @Override
    public boolean atualizarStatusSeAtual(Long id, StatusConvite statusEsperado, StatusConvite statusNovo) {
        return jpaRepository.atualizarStatusSeAtual(id, statusEsperado, statusNovo) > 0;
    }

    @Override
    public void excluirTudo() {
        jpaRepository.deleteAll();
    }

    private Convite paraDomain(ConviteJpaEntity entidade) {
        return new Convite(
                entidade.getId(), entidade.getVagaId(), entidade.getPacienteId(), entidade.getEnviadoEm(),
                entidade.getExpiraEm(), entidade.getStatus(), entidade.getOrdemNaCascata());
    }
}
