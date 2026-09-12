package br.com.fiap.vagazero.agenda.infrastructure.persistencia;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.fiap.vagazero.agenda.domain.Agendamento;
import br.com.fiap.vagazero.agenda.domain.AgendamentoRepositorio;

@Repository
public class AgendamentoRepositorioAdapter implements AgendamentoRepositorio {

    private final AgendamentoJpaRepository jpaRepository;
    private final Clock clock;

    public AgendamentoRepositorioAdapter(AgendamentoJpaRepository jpaRepository, Clock clock) {
        this.jpaRepository = jpaRepository;
        this.clock = clock;
    }

    @Override
    public Agendamento salvar(Agendamento agendamento) {
        LocalDateTime criadoEm = agendamento.id() == null
                ? LocalDateTime.now(clock)
                : jpaRepository.findById(agendamento.id())
                        .map(AgendamentoJpaEntity::getCriadoEm)
                        .orElse(LocalDateTime.now(clock));
        AgendamentoJpaEntity entidade = new AgendamentoJpaEntity(
                agendamento.id(), agendamento.vagaId(), agendamento.pacienteId(), agendamento.status(),
                agendamento.confirmadoEm(), criadoEm);
        return paraDomain(jpaRepository.save(entidade));
    }

    @Override
    public Optional<Agendamento> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::paraDomain);
    }

    @Override
    public List<Agendamento> listarTodos() {
        return jpaRepository.findAll().stream().map(this::paraDomain).toList();
    }

    @Override
    public void excluir(Long id) {
        jpaRepository.deleteById(id);
    }

    private Agendamento paraDomain(AgendamentoJpaEntity entidade) {
        return new Agendamento(
                entidade.getId(), entidade.getVagaId(), entidade.getPacienteId(),
                entidade.getStatus(), entidade.getConfirmadoEm());
    }
}
