package br.com.fiap.vagazero.risco.infrastructure.persistencia;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.fiap.vagazero.risco.domain.AvaliacaoRisco;
import br.com.fiap.vagazero.risco.domain.AvaliacaoRiscoRepositorio;
import br.com.fiap.vagazero.risco.domain.FatorRisco;

@Repository
public class AvaliacaoRiscoRepositorioAdapter implements AvaliacaoRiscoRepositorio {

    private final AvaliacaoRiscoJpaRepository jpaRepository;
    private final AvaliacaoRiscoFatorJpaRepository fatorJpaRepository;

    public AvaliacaoRiscoRepositorioAdapter(
            AvaliacaoRiscoJpaRepository jpaRepository, AvaliacaoRiscoFatorJpaRepository fatorJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.fatorJpaRepository = fatorJpaRepository;
    }

    @Override
    public AvaliacaoRisco salvar(AvaliacaoRisco avaliacao) {
        AvaliacaoRiscoJpaEntity entidade = new AvaliacaoRiscoJpaEntity(
                avaliacao.id(), avaliacao.agendamentoId(), avaliacao.score(), avaliacao.classificacao(),
                avaliacao.avaliadoEm());
        AvaliacaoRiscoJpaEntity salva = jpaRepository.save(entidade);

        List<FatorRisco> fatoresSalvos = avaliacao.fatores().stream()
                .map(fator -> {
                    AvaliacaoRiscoFatorJpaEntity fatorEntidade = new AvaliacaoRiscoFatorJpaEntity(
                            null, salva.getId(), fator.codigo(), fator.descricao(), fator.pontos());
                    fatorJpaRepository.save(fatorEntidade);
                    return fator;
                })
                .toList();

        return new AvaliacaoRisco(
                salva.getId(), salva.getAgendamentoId(), salva.getScore(), salva.getClassificacao(),
                salva.getAvaliadoEm(), fatoresSalvos);
    }

    @Override
    public Optional<AvaliacaoRisco> buscarUltimaPorAgendamento(Long agendamentoId) {
        return jpaRepository.findByAgendamentoIdOrderByAvaliadoEmDescIdDesc(agendamentoId).stream()
                .findFirst()
                .map(this::paraDomain);
    }

    private AvaliacaoRisco paraDomain(AvaliacaoRiscoJpaEntity entidade) {
        List<FatorRisco> fatores = fatorJpaRepository.findByAvaliacaoRiscoId(entidade.getId()).stream()
                .map(f -> new FatorRisco(f.getCodigo(), f.getDescricao(), f.getPontos()))
                .toList();
        return new AvaliacaoRisco(
                entidade.getId(), entidade.getAgendamentoId(), entidade.getScore(), entidade.getClassificacao(),
                entidade.getAvaliadoEm(), fatores);
    }
}
