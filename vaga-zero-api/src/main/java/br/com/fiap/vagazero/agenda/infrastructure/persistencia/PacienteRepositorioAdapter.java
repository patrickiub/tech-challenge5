package br.com.fiap.vagazero.agenda.infrastructure.persistencia;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.fiap.vagazero.agenda.domain.Paciente;
import br.com.fiap.vagazero.agenda.domain.PacienteRepositorio;

@Repository
public class PacienteRepositorioAdapter implements PacienteRepositorio {

    private final PacienteJpaRepository jpaRepository;
    private final Clock clock;

    public PacienteRepositorioAdapter(PacienteJpaRepository jpaRepository, Clock clock) {
        this.jpaRepository = jpaRepository;
        this.clock = clock;
    }

    @Override
    public Paciente salvar(Paciente paciente) {
        LocalDateTime criadoEm = paciente.id() == null
                ? LocalDateTime.now(clock)
                : jpaRepository.findById(paciente.id())
                        .map(PacienteJpaEntity::getCriadoEm)
                        .orElse(LocalDateTime.now(clock));
        PacienteJpaEntity entidade = new PacienteJpaEntity(
                paciente.id(), paciente.nome(), paciente.cns(), paciente.telefone(),
                paciente.latitude(), paciente.longitude(), paciente.dataNascimento(), criadoEm);
        return paraDomain(jpaRepository.save(entidade));
    }

    @Override
    public Optional<Paciente> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::paraDomain);
    }

    @Override
    public List<Paciente> listarTodos() {
        return jpaRepository.findAll().stream().map(this::paraDomain).toList();
    }

    @Override
    public void excluir(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorCns(String cns) {
        return jpaRepository.existsByCns(cns);
    }

    private Paciente paraDomain(PacienteJpaEntity entidade) {
        return new Paciente(
                entidade.getId(), entidade.getNome(), entidade.getCns(), entidade.getTelefone(),
                entidade.getLatitude(), entidade.getLongitude(), entidade.getDataNascimento());
    }
}
