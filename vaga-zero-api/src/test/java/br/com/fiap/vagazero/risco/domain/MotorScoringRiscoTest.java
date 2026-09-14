package br.com.fiap.vagazero.risco.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MotorScoringRiscoTest {

    /**
     * Pesos exatamente como definidos no CLAUDE.md (tabela de scoring).
     */
    private static final PesosScoring PESOS_PADRAO = new PesosScoring(
            25, 50, 10.0, 15, 30, 10, 10, 18, 30, 5, -40, 30, 60);

    private final MotorScoringRisco motor = new MotorScoringRisco(PESOS_PADRAO);

    private EntradaScoring baseline() {
        return new EntradaScoring(0, 5.0, 10, false, 50, false);
    }

    @Test
    void semNenhumFatorScoreZeroBaixoSemFatoresListados() {
        ResultadoScoring resultado = motor.avaliar(baseline());

        assertThat(resultado.score()).isZero();
        assertThat(resultado.classificacao()).isEqualTo(ClassificacaoRisco.BAIXO);
        assertThat(resultado.fatores()).isEmpty();
    }

    @Test
    void umaFaltaNosUltimos12MesesSoma25Pontos() {
        EntradaScoring entrada = new EntradaScoring(1, 5.0, 10, false, 50, false);

        ResultadoScoring resultado = motor.avaliar(entrada);

        assertThat(resultado.score()).isEqualTo(25);
        assertThat(resultado.fatores()).extracting("codigo").containsExactly("FALTAS_RECENTES");
        assertThat(resultado.fatores().get(0).pontos()).isEqualTo(25);
    }

    @Test
    void tresFaltasRespeitamTetoDe50Pontos() {
        EntradaScoring entrada = new EntradaScoring(3, 5.0, 10, false, 50, false);

        ResultadoScoring resultado = motor.avaliar(entrada);

        assertThat(resultado.score()).isEqualTo(50);
        assertThat(resultado.fatores().get(0).pontos()).isEqualTo(50);
    }

    @Test
    void distanciaAcimaDoLimiteSoma15Pontos() {
        EntradaScoring entrada = new EntradaScoring(0, 10.01, 10, false, 50, false);

        ResultadoScoring resultado = motor.avaliar(entrada);

        assertThat(resultado.score()).isEqualTo(15);
        assertThat(resultado.fatores()).extracting("codigo").containsExactly("DISTANCIA_ACIMA_DO_LIMITE");
    }

    @Test
    void distanciaExatamenteNoLimiteNaoSoma() {
        EntradaScoring entrada = new EntradaScoring(0, 10.0, 10, false, 50, false);

        ResultadoScoring resultado = motor.avaliar(entrada);

        assertThat(resultado.score()).isZero();
        assertThat(resultado.fatores()).isEmpty();
    }

    @Test
    void antecedenciaAcimaDoLimiteSoma10Pontos() {
        EntradaScoring entrada = new EntradaScoring(0, 5.0, 31, false, 50, false);

        ResultadoScoring resultado = motor.avaliar(entrada);

        assertThat(resultado.score()).isEqualTo(10);
        assertThat(resultado.fatores()).extracting("codigo").containsExactly("ANTECEDENCIA_ACIMA_DO_LIMITE");
    }

    @Test
    void antecedenciaExatamenteNoLimiteNaoSoma() {
        EntradaScoring entrada = new EntradaScoring(0, 5.0, 30, false, 50, false);

        ResultadoScoring resultado = motor.avaliar(entrada);

        assertThat(resultado.score()).isZero();
    }

    @Test
    void primeiraConsultaNaEspecialidadeSoma10Pontos() {
        EntradaScoring entrada = new EntradaScoring(0, 5.0, 10, true, 50, false);

        ResultadoScoring resultado = motor.avaliar(entrada);

        assertThat(resultado.score()).isEqualTo(10);
        assertThat(resultado.fatores()).extracting("codigo").containsExactly("PRIMEIRA_CONSULTA_ESPECIALIDADE");
    }

    @Test
    void idadeDentroDaFaixa18a30Soma5Pontos() {
        EntradaScoring entrada = new EntradaScoring(0, 5.0, 10, false, 25, false);

        ResultadoScoring resultado = motor.avaliar(entrada);

        assertThat(resultado.score()).isEqualTo(5);
        assertThat(resultado.fatores()).extracting("codigo").containsExactly("IDADE_FAIXA_RISCO");
    }

    @Test
    void idadeNosLimitesDaFaixaSoma5Pontos() {
        assertThat(motor.avaliar(new EntradaScoring(0, 5.0, 10, false, 18, false)).score()).isEqualTo(5);
        assertThat(motor.avaliar(new EntradaScoring(0, 5.0, 10, false, 30, false)).score()).isEqualTo(5);
    }

    @Test
    void idadeForaDaFaixaNaoSoma() {
        assertThat(motor.avaliar(new EntradaScoring(0, 5.0, 10, false, 17, false)).score()).isZero();
        assertThat(motor.avaliar(new EntradaScoring(0, 5.0, 10, false, 31, false)).score()).isZero();
    }

    @Test
    void confirmacaoDePresencaReduz40PontosMasScoreNaoFicaNegativo() {
        EntradaScoring entrada = new EntradaScoring(0, 5.0, 10, false, 50, true);

        ResultadoScoring resultado = motor.avaliar(entrada);

        assertThat(resultado.score()).isZero();
        assertThat(resultado.classificacao()).isEqualTo(ClassificacaoRisco.BAIXO);
        assertThat(resultado.fatores()).extracting("codigo").containsExactly("CONFIRMACAO_PRESENCA");
        assertThat(resultado.fatores().get(0).pontos()).isEqualTo(-40);
    }

    @Test
    void confirmacaoDePresencaReduzScoreDeUmaAvaliacaoAlta() {
        EntradaScoring semConfirmar = new EntradaScoring(2, 22.0, 10, false, 50, false);
        EntradaScoring confirmando = new EntradaScoring(2, 22.0, 10, false, 50, true);

        int scoreSemConfirmar = motor.avaliar(semConfirmar).score();
        int scoreConfirmando = motor.avaliar(confirmando).score();

        assertThat(scoreSemConfirmar).isEqualTo(65);
        assertThat(scoreConfirmando).isEqualTo(25);
    }

    @Test
    void combinacaoDeFatoresSomaTodosOsPontosEListaTodosOsFatores() {
        // 1 falta (+25) + distancia acima do limite (+15) + antecedencia acima do limite (+10)
        // + primeira consulta (+10) + idade na faixa (+5) = 65 -> ALTO
        EntradaScoring entrada = new EntradaScoring(1, 15.0, 40, true, 25, false);

        ResultadoScoring resultado = motor.avaliar(entrada);

        assertThat(resultado.score()).isEqualTo(65);
        assertThat(resultado.classificacao()).isEqualTo(ClassificacaoRisco.ALTO);
        assertThat(resultado.fatores()).hasSize(5);
    }

    @Test
    void classificacaoBaixaDeZeroA29() {
        PesosScoring pesosControlados = pesosSoContagemDeFaltas();
        MotorScoringRisco motorControlado = new MotorScoringRisco(pesosControlados);

        ResultadoScoring resultado = motorControlado.avaliar(entradaComScoreBruto(29));

        assertThat(resultado.score()).isEqualTo(29);
        assertThat(resultado.classificacao()).isEqualTo(ClassificacaoRisco.BAIXO);
    }

    @Test
    void classificacaoMediaNoLimiteInferior30() {
        PesosScoring pesosControlados = pesosSoContagemDeFaltas();
        MotorScoringRisco motorControlado = new MotorScoringRisco(pesosControlados);

        ResultadoScoring resultado = motorControlado.avaliar(entradaComScoreBruto(30));

        assertThat(resultado.score()).isEqualTo(30);
        assertThat(resultado.classificacao()).isEqualTo(ClassificacaoRisco.MEDIO);
    }

    @Test
    void classificacaoMediaNoLimiteSuperior59() {
        PesosScoring pesosControlados = pesosSoContagemDeFaltas();
        MotorScoringRisco motorControlado = new MotorScoringRisco(pesosControlados);

        ResultadoScoring resultado = motorControlado.avaliar(entradaComScoreBruto(59));

        assertThat(resultado.score()).isEqualTo(59);
        assertThat(resultado.classificacao()).isEqualTo(ClassificacaoRisco.MEDIO);
    }

    @Test
    void classificacaoAltaNoLimiteInferior60() {
        PesosScoring pesosControlados = pesosSoContagemDeFaltas();
        MotorScoringRisco motorControlado = new MotorScoringRisco(pesosControlados);

        ResultadoScoring resultado = motorControlado.avaliar(entradaComScoreBruto(60));

        assertThat(resultado.score()).isEqualTo(60);
        assertThat(resultado.classificacao()).isEqualTo(ClassificacaoRisco.ALTO);
    }

    /**
     * Pesos onde so o fator de faltas conta (1 ponto por falta, sem teto
     * relevante, demais fatores neutralizados por limiares inalcancaveis) -
     * permite montar um score exato para testar as fronteiras de
     * classificacao isoladamente.
     */
    private PesosScoring pesosSoContagemDeFaltas() {
        return new PesosScoring(1, 1000, 999.0, 0, 999, 0, 0, 999, -1, 0, 0, 30, 60);
    }

    private EntradaScoring entradaComScoreBruto(int scoreDesejado) {
        return new EntradaScoring(scoreDesejado, 0.0, 0, false, 0, false);
    }
}
