package br.com.fiap.vagazero.fila.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class ServicoElegibilidadeCascataTest {

    private static final BigDecimal UNIDADE_LAT = BigDecimal.ZERO;
    private static final BigDecimal UNIDADE_LON = BigDecimal.ZERO;

    private final ServicoElegibilidadeCascata servico = new ServicoElegibilidadeCascata();

    private ItemFila item(
            long pacienteId, String especialidade, LocalDateTime dataEntrada, int prioridade,
            boolean aceitaChamadoImediato, double raioMaximoKm) {
        return new ItemFila(
                pacienteId, pacienteId, especialidade, dataEntrada, prioridade, aceitaChamadoImediato,
                BigDecimal.valueOf(raioMaximoKm));
    }

    private LocalizacaoPaciente localizacaoAdistanciaKm(long pacienteId, double distanciaAproxKm) {
        // 1 grau de latitude ~ 111km no equador; usamos isso para controlar a distancia aproximada
        double graus = distanciaAproxKm / 111.0;
        return new LocalizacaoPaciente(pacienteId, "Paciente " + pacienteId, BigDecimal.valueOf(graus), BigDecimal.ZERO);
    }

    @Test
    void filtraPorEspecialidadeDiferente() {
        ItemFila fora = item(1L, "Cardiologia", LocalDateTime.now(), 5, true, 50);
        Map<Long, LocalizacaoPaciente> localizacoes = Map.of(1L, localizacaoAdistanciaKm(1L, 5));

        List<CandidatoElegivel> resultado = servico.selecionarCandidatos(
                List.of(fora), "Oftalmologia", UNIDADE_LAT, UNIDADE_LON, localizacoes);

        assertThat(resultado).isEmpty();
    }

    @Test
    void filtraQuemNaoAceitaChamadoImediato() {
        ItemFila item = item(1L, "Oftalmologia", LocalDateTime.now(), 5, false, 50);
        Map<Long, LocalizacaoPaciente> localizacoes = Map.of(1L, localizacaoAdistanciaKm(1L, 5));

        List<CandidatoElegivel> resultado = servico.selecionarCandidatos(
                List.of(item), "Oftalmologia", UNIDADE_LAT, UNIDADE_LON, localizacoes);

        assertThat(resultado).isEmpty();
    }

    @Test
    void filtraQuemEstaForaDoRaioMaximo() {
        ItemFila item = item(1L, "Oftalmologia", LocalDateTime.now(), 5, true, 10);
        Map<Long, LocalizacaoPaciente> localizacoes = Map.of(1L, localizacaoAdistanciaKm(1L, 20));

        List<CandidatoElegivel> resultado = servico.selecionarCandidatos(
                List.of(item), "Oftalmologia", UNIDADE_LAT, UNIDADE_LON, localizacoes);

        assertThat(resultado).isEmpty();
    }

    @Test
    void mantemQuemEstaDentroDoRaio() {
        ItemFila item = item(1L, "Oftalmologia", LocalDateTime.now(), 5, true, 10);
        Map<Long, LocalizacaoPaciente> localizacoes = Map.of(1L, localizacaoAdistanciaKm(1L, 5));

        List<CandidatoElegivel> resultado = servico.selecionarCandidatos(
                List.of(item), "Oftalmologia", UNIDADE_LAT, UNIDADE_LON, localizacoes);

        assertThat(resultado).hasSize(1);
    }

    @Test
    void ordenaPorPrioridadeClinicaDecrescente() {
        LocalDateTime agora = LocalDateTime.now();
        ItemFila baixaPrioridade = item(1L, "Oftalmologia", agora, 2, true, 50);
        ItemFila altaPrioridade = item(2L, "Oftalmologia", agora, 5, true, 50);
        Map<Long, LocalizacaoPaciente> localizacoes = Map.of(
                1L, localizacaoAdistanciaKm(1L, 5),
                2L, localizacaoAdistanciaKm(2L, 5));

        List<CandidatoElegivel> resultado = servico.selecionarCandidatos(
                List.of(baixaPrioridade, altaPrioridade), "Oftalmologia", UNIDADE_LAT, UNIDADE_LON, localizacoes);

        assertThat(resultado).extracting(c -> c.itemFila().pacienteId()).containsExactly(2L, 1L);
    }

    @Test
    void empatePrioridadeDesempataPorTempoDeEsperaMaisAntigoPrimeiro() {
        LocalDateTime maisAntigo = LocalDateTime.now().minusDays(30);
        LocalDateTime maisRecente = LocalDateTime.now().minusDays(1);
        ItemFila entrouRecente = item(1L, "Oftalmologia", maisRecente, 3, true, 50);
        ItemFila entrouAntigo = item(2L, "Oftalmologia", maisAntigo, 3, true, 50);
        Map<Long, LocalizacaoPaciente> localizacoes = Map.of(
                1L, localizacaoAdistanciaKm(1L, 5),
                2L, localizacaoAdistanciaKm(2L, 5));

        List<CandidatoElegivel> resultado = servico.selecionarCandidatos(
                List.of(entrouRecente, entrouAntigo), "Oftalmologia", UNIDADE_LAT, UNIDADE_LON, localizacoes);

        assertThat(resultado).extracting(c -> c.itemFila().pacienteId()).containsExactly(2L, 1L);
    }

    @Test
    void empatePrioridadeETempoDesempataPorDistanciaCrescente() {
        LocalDateTime mesmaEntrada = LocalDateTime.now().minusDays(10);
        ItemFila longe = item(1L, "Oftalmologia", mesmaEntrada, 3, true, 50);
        ItemFila perto = item(2L, "Oftalmologia", mesmaEntrada, 3, true, 50);
        Map<Long, LocalizacaoPaciente> localizacoes = Map.of(
                1L, localizacaoAdistanciaKm(1L, 20),
                2L, localizacaoAdistanciaKm(2L, 5));

        List<CandidatoElegivel> resultado = servico.selecionarCandidatos(
                List.of(longe, perto), "Oftalmologia", UNIDADE_LAT, UNIDADE_LON, localizacoes);

        assertThat(resultado).extracting(c -> c.itemFila().pacienteId()).containsExactly(2L, 1L);
    }
}
