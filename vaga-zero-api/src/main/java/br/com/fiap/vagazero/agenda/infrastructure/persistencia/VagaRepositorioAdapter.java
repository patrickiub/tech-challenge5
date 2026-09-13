package br.com.fiap.vagazero.agenda.infrastructure.persistencia;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.fiap.vagazero.agenda.domain.StatusVaga;
import br.com.fiap.vagazero.agenda.domain.Vaga;
import br.com.fiap.vagazero.agenda.domain.VagaRepositorio;

@Repository
public class VagaRepositorioAdapter implements VagaRepositorio {

    private final VagaJpaRepository jpaRepository;
    private final Clock clock;

    public VagaRepositorioAdapter(VagaJpaRepository jpaRepository, Clock clock) {
        this.jpaRepository = jpaRepository;
        this.clock = clock;
    }

    @Override
    public Vaga salvar(Vaga vaga) {
        LocalDateTime criadoEm = vaga.id() == null
                ? LocalDateTime.now(clock)
                : jpaRepository.findById(vaga.id())
                        .map(VagaJpaEntity::getCriadoEm)
                        .orElse(LocalDateTime.now(clock));
        VagaJpaEntity entidade = new VagaJpaEntity(
                vaga.id(), vaga.unidadeId(), vaga.especialidade(), vaga.profissional(),
                vaga.dataHora(), vaga.status(), criadoEm);
        return paraDomain(jpaRepository.save(entidade));
    }

    @Override
    public Optional<Vaga> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::paraDomain);
    }

    @Override
    public List<Vaga> listarTodas() {
        return jpaRepository.findAll().stream().map(this::paraDomain).toList();
    }

    @Override
    public void excluir(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean mudarStatusSeAtual(Long id, StatusVaga statusEsperado, StatusVaga statusNovo) {
        return jpaRepository.mudarStatusSeAtual(id, statusEsperado, statusNovo) > 0;
    }

    private Vaga paraDomain(VagaJpaEntity entidade) {
        return new Vaga(
                entidade.getId(), entidade.getUnidadeId(), entidade.getEspecialidade(),
                entidade.getProfissional(), entidade.getDataHora(), entidade.getStatus());
    }
}
