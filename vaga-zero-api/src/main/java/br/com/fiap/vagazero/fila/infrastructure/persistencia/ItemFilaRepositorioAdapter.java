package br.com.fiap.vagazero.fila.infrastructure.persistencia;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.fiap.vagazero.fila.domain.ItemFila;
import br.com.fiap.vagazero.fila.domain.ItemFilaRepositorio;

@Repository
public class ItemFilaRepositorioAdapter implements ItemFilaRepositorio {

    private final ItemFilaJpaRepository jpaRepository;
    private final Clock clock;

    public ItemFilaRepositorioAdapter(ItemFilaJpaRepository jpaRepository, Clock clock) {
        this.jpaRepository = jpaRepository;
        this.clock = clock;
    }

    @Override
    public ItemFila salvar(ItemFila itemFila) {
        LocalDateTime dataEntrada = itemFila.dataEntrada() != null
                ? itemFila.dataEntrada()
                : LocalDateTime.now(clock);
        ItemFilaJpaEntity entidade = new ItemFilaJpaEntity(
                itemFila.id(), itemFila.pacienteId(), itemFila.especialidade(), dataEntrada,
                (short) itemFila.prioridadeClinica(), itemFila.aceitaChamadoImediato(), itemFila.raioMaximoKm());
        return paraDomain(jpaRepository.save(entidade));
    }

    @Override
    public Optional<ItemFila> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::paraDomain);
    }

    @Override
    public List<ItemFila> listarTodos() {
        return jpaRepository.findAll().stream().map(this::paraDomain).toList();
    }

    @Override
    public List<ItemFila> listarPorEspecialidade(String especialidade) {
        return jpaRepository.findByEspecialidade(especialidade).stream().map(this::paraDomain).toList();
    }

    @Override
    public Optional<ItemFila> buscarPorPacienteEEspecialidade(Long pacienteId, String especialidade) {
        return jpaRepository.findByPacienteIdAndEspecialidade(pacienteId, especialidade).map(this::paraDomain);
    }

    @Override
    public void excluir(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void excluirTudo() {
        jpaRepository.deleteAll();
    }

    private ItemFila paraDomain(ItemFilaJpaEntity entidade) {
        return new ItemFila(
                entidade.getId(), entidade.getPacienteId(), entidade.getEspecialidade(),
                entidade.getDataEntrada(), entidade.getPrioridadeClinica(), entidade.isAceitaChamadoImediato(),
                entidade.getRaioMaximoKm());
    }
}
