package br.com.fiap.vagazero.fila.domain;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import br.com.fiap.vagazero.shared.geo.CalculadoraDistancia;

/**
 * Seleciona e ordena os candidatos elegiveis a uma vaga liberada.
 *
 * Elegibilidade: mesma especialidade da vaga, aceitaChamadoImediato = true e
 * distancia ate a unidade dentro do raioMaximoKm do paciente.
 *
 * Ordenacao: prioridadeClinica desc, tempo de espera desc (mais antigo
 * primeiro - equivale a dataEntrada asc), distancia asc.
 */
public class ServicoElegibilidadeCascata {

    public List<CandidatoElegivel> selecionarCandidatos(
            List<ItemFila> itensFila,
            String especialidade,
            BigDecimal unidadeLatitude,
            BigDecimal unidadeLongitude,
            Map<Long, LocalizacaoPaciente> localizacaoPorPaciente) {

        return itensFila.stream()
                .filter(item -> item.especialidade().equalsIgnoreCase(especialidade))
                .filter(ItemFila::aceitaChamadoImediato)
                .map(item -> paraCandidato(item, unidadeLatitude, unidadeLongitude, localizacaoPorPaciente))
                .filter(candidato -> candidato.distanciaKm() <= candidato.itemFila().raioMaximoKm().doubleValue())
                .sorted(Comparator
                        .comparingInt((CandidatoElegivel c) -> c.itemFila().prioridadeClinica())
                        .reversed()
                        .thenComparing(c -> c.itemFila().dataEntrada())
                        .thenComparingDouble(CandidatoElegivel::distanciaKm))
                .toList();
    }

    private CandidatoElegivel paraCandidato(
            ItemFila item, BigDecimal unidadeLatitude, BigDecimal unidadeLongitude,
            Map<Long, LocalizacaoPaciente> localizacaoPorPaciente) {
        LocalizacaoPaciente localizacao = localizacaoPorPaciente.get(item.pacienteId());
        double distanciaKm = CalculadoraDistancia.haversineKm(
                unidadeLatitude, unidadeLongitude, localizacao.latitude(), localizacao.longitude());
        return new CandidatoElegivel(item, localizacao, distanciaKm);
    }
}
