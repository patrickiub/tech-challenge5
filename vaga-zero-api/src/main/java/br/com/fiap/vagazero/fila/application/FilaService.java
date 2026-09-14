package br.com.fiap.vagazero.fila.application;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.vagazero.fila.domain.ItemFila;
import br.com.fiap.vagazero.fila.domain.ItemFilaNaoEncontradoException;
import br.com.fiap.vagazero.fila.domain.ItemFilaRepositorio;

@Service
public class FilaService {

    private final ItemFilaRepositorio itemFilaRepositorio;
    private final Clock clock;

    public FilaService(ItemFilaRepositorio itemFilaRepositorio, Clock clock) {
        this.itemFilaRepositorio = itemFilaRepositorio;
        this.clock = clock;
    }

    public ItemFila entrar(
            Long pacienteId, String especialidade, int prioridadeClinica, boolean aceitaChamadoImediato,
            BigDecimal raioMaximoKm) {
        return entrar(pacienteId, especialidade, prioridadeClinica, aceitaChamadoImediato, raioMaximoKm,
                LocalDateTime.now(clock));
    }

    /**
     * Variante usada pelo modulo demo para semear a fila com tempos de
     * espera variados (dataEntrada no passado), exercitando o desempate por
     * tempo de espera na ordenacao da cascata.
     */
    public ItemFila entrar(
            Long pacienteId, String especialidade, int prioridadeClinica, boolean aceitaChamadoImediato,
            BigDecimal raioMaximoKm, LocalDateTime dataEntrada) {
        ItemFila item = new ItemFila(
                null, pacienteId, especialidade, dataEntrada, prioridadeClinica, aceitaChamadoImediato,
                raioMaximoKm);
        return itemFilaRepositorio.salvar(item);
    }

    public ItemFila buscarPorId(Long id) {
        return itemFilaRepositorio.buscarPorId(id)
                .orElseThrow(() -> new ItemFilaNaoEncontradoException(id));
    }

    public List<ItemFila> listar(String especialidade) {
        return especialidade == null
                ? itemFilaRepositorio.listarTodos()
                : itemFilaRepositorio.listarPorEspecialidade(especialidade);
    }

    public void sair(Long id) {
        buscarPorId(id);
        itemFilaRepositorio.excluir(id);
    }
}
