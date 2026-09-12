package br.com.fiap.vagazero.agenda.infrastructure.persistencia;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.fiap.vagazero.agenda.domain.Unidade;
import br.com.fiap.vagazero.agenda.domain.UnidadeRepositorio;

@Repository
public class UnidadeRepositorioAdapter implements UnidadeRepositorio {

    private final UnidadeJpaRepository jpaRepository;
    private final Clock clock;

    public UnidadeRepositorioAdapter(UnidadeJpaRepository jpaRepository, Clock clock) {
        this.jpaRepository = jpaRepository;
        this.clock = clock;
    }

    @Override
    public Unidade salvar(Unidade unidade) {
        LocalDateTime criadoEm = unidade.id() == null
                ? LocalDateTime.now(clock)
                : jpaRepository.findById(unidade.id())
                        .map(UnidadeJpaEntity::getCriadoEm)
                        .orElse(LocalDateTime.now(clock));
        UnidadeJpaEntity entidade = new UnidadeJpaEntity(
                unidade.id(), unidade.nome(), unidade.latitude(), unidade.longitude(), criadoEm);
        return paraDomain(jpaRepository.save(entidade));
    }

    @Override
    public Optional<Unidade> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::paraDomain);
    }

    @Override
    public List<Unidade> listarTodas() {
        return jpaRepository.findAll().stream().map(this::paraDomain).toList();
    }

    @Override
    public void excluir(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorId(Long id) {
        return jpaRepository.existsById(id);
    }

    private Unidade paraDomain(UnidadeJpaEntity entidade) {
        return new Unidade(entidade.getId(), entidade.getNome(), entidade.getLatitude(), entidade.getLongitude());
    }
}
