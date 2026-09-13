package br.com.fiap.vagazero.agenda.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.vagazero.agenda.domain.StatusVaga;
import br.com.fiap.vagazero.agenda.domain.UnidadeNaoEncontradaException;
import br.com.fiap.vagazero.agenda.domain.UnidadeRepositorio;
import br.com.fiap.vagazero.agenda.domain.Vaga;
import br.com.fiap.vagazero.agenda.domain.VagaNaoEncontradaException;
import br.com.fiap.vagazero.agenda.domain.VagaRepositorio;

@Service
public class VagaService implements VagaCascataUseCase {

    private final VagaRepositorio vagaRepositorio;
    private final UnidadeRepositorio unidadeRepositorio;

    public VagaService(VagaRepositorio vagaRepositorio, UnidadeRepositorio unidadeRepositorio) {
        this.vagaRepositorio = vagaRepositorio;
        this.unidadeRepositorio = unidadeRepositorio;
    }

    public Vaga criar(Long unidadeId, String especialidade, String profissional, LocalDateTime dataHora) {
        if (!unidadeRepositorio.existePorId(unidadeId)) {
            throw new UnidadeNaoEncontradaException(unidadeId);
        }
        Vaga vaga = new Vaga(null, unidadeId, especialidade, profissional, dataHora, StatusVaga.DISPONIVEL);
        return vagaRepositorio.salvar(vaga);
    }

    @Override
    public Vaga buscarPorId(Long id) {
        return vagaRepositorio.buscarPorId(id)
                .orElseThrow(() -> new VagaNaoEncontradaException(id));
    }

    @Override
    public boolean mudarStatusSeAtual(Long id, StatusVaga statusEsperado, StatusVaga statusNovo) {
        return vagaRepositorio.mudarStatusSeAtual(id, statusEsperado, statusNovo);
    }

    public List<Vaga> listarTodas() {
        return vagaRepositorio.listarTodas();
    }

    public Vaga atualizar(
            Long id, String especialidade, String profissional, LocalDateTime dataHora, StatusVaga status) {
        Vaga existente = buscarPorId(id);
        Vaga atualizada = new Vaga(id, existente.unidadeId(), especialidade, profissional, dataHora, status);
        return vagaRepositorio.salvar(atualizada);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        vagaRepositorio.excluir(id);
    }
}
