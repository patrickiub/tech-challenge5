package br.com.fiap.vagazero.agenda.application;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.vagazero.agenda.domain.Unidade;
import br.com.fiap.vagazero.agenda.domain.UnidadeNaoEncontradaException;
import br.com.fiap.vagazero.agenda.domain.UnidadeRepositorio;

@Service
public class UnidadeService implements ConsultaUnidadeUseCase {

    private final UnidadeRepositorio unidadeRepositorio;

    public UnidadeService(UnidadeRepositorio unidadeRepositorio) {
        this.unidadeRepositorio = unidadeRepositorio;
    }

    public Unidade criar(String nome, BigDecimal latitude, BigDecimal longitude) {
        return unidadeRepositorio.salvar(new Unidade(null, nome, latitude, longitude));
    }

    public Unidade buscarPorId(Long id) {
        return unidadeRepositorio.buscarPorId(id)
                .orElseThrow(() -> new UnidadeNaoEncontradaException(id));
    }

    public List<Unidade> listarTodas() {
        return unidadeRepositorio.listarTodas();
    }

    public Unidade atualizar(Long id, String nome, BigDecimal latitude, BigDecimal longitude) {
        buscarPorId(id);
        return unidadeRepositorio.salvar(new Unidade(id, nome, latitude, longitude));
    }

    public void excluir(Long id) {
        buscarPorId(id);
        unidadeRepositorio.excluir(id);
    }

    @Override
    public UnidadeResumo buscarResumo(Long unidadeId) {
        Unidade unidade = buscarPorId(unidadeId);
        return new UnidadeResumo(unidade.id(), unidade.nome(), unidade.latitude(), unidade.longitude());
    }
}
