package br.com.fiap.vagazero.risco.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Motor de regras deterministico do score de risco de falta. Sem
 * dependencia de framework: os pesos e limiares sao injetados via
 * PesosScoring, montado pela camada de aplicacao a partir de
 * application.yml (vagazero.scoring.*).
 */
public class MotorScoringRisco {

    private final PesosScoring pesos;

    public MotorScoringRisco(PesosScoring pesos) {
        this.pesos = pesos;
    }

    public ResultadoScoring avaliar(EntradaScoring entrada) {
        List<FatorRisco> fatores = new ArrayList<>();
        int score = 0;

        int pontosFaltas = Math.min(
                entrada.faltasUltimos12Meses() * pesos.pontosPorFalta(), pesos.tetoPontosFaltas());
        if (pontosFaltas != 0) {
            fatores.add(new FatorRisco(
                    "FALTAS_RECENTES",
                    entrada.faltasUltimos12Meses() + " falta(s) nos ultimos 12 meses",
                    pontosFaltas));
            score += pontosFaltas;
        }

        if (entrada.distanciaKm() > pesos.distanciaLimiteKm()) {
            fatores.add(new FatorRisco(
                    "DISTANCIA_ACIMA_DO_LIMITE",
                    "Distancia ate a unidade (" + arredondar(entrada.distanciaKm()) + "km) acima de "
                            + pesos.distanciaLimiteKm() + "km",
                    pesos.pontosDistanciaAcimaDoLimite()));
            score += pesos.pontosDistanciaAcimaDoLimite();
        }

        if (entrada.diasAntecedencia() > pesos.antecedenciaLimiteDias()) {
            fatores.add(new FatorRisco(
                    "ANTECEDENCIA_ACIMA_DO_LIMITE",
                    "Marcacao feita com " + entrada.diasAntecedencia() + " dia(s) de antecedencia, acima de "
                            + pesos.antecedenciaLimiteDias() + " dias",
                    pesos.pontosAntecedenciaAcimaDoLimite()));
            score += pesos.pontosAntecedenciaAcimaDoLimite();
        }

        if (entrada.primeiraConsultaNaEspecialidade()) {
            fatores.add(new FatorRisco(
                    "PRIMEIRA_CONSULTA_ESPECIALIDADE",
                    "Primeira consulta do paciente nessa especialidade",
                    pesos.pontosPrimeiraConsultaEspecialidade()));
            score += pesos.pontosPrimeiraConsultaEspecialidade();
        }

        if (entrada.idade() >= pesos.idadeMinima() && entrada.idade() <= pesos.idadeMaxima()) {
            fatores.add(new FatorRisco(
                    "IDADE_FAIXA_RISCO",
                    "Paciente com " + entrada.idade() + " anos, entre " + pesos.idadeMinima() + " e "
                            + pesos.idadeMaxima(),
                    pesos.pontosFaixaEtaria()));
            score += pesos.pontosFaixaEtaria();
        }

        if (entrada.confirmouPresenca()) {
            fatores.add(new FatorRisco(
                    "CONFIRMACAO_PRESENCA",
                    "Paciente confirmou presenca ativamente",
                    pesos.pontosConfirmacaoPresenca()));
            score += pesos.pontosConfirmacaoPresenca();
        }

        score = Math.max(score, 0);
        ClassificacaoRisco classificacao = classificar(score);

        return new ResultadoScoring(score, classificacao, fatores);
    }

    private ClassificacaoRisco classificar(int score) {
        if (score >= pesos.limiteMedioAlto()) {
            return ClassificacaoRisco.ALTO;
        }
        if (score >= pesos.limiteBaixoMedio()) {
            return ClassificacaoRisco.MEDIO;
        }
        return ClassificacaoRisco.BAIXO;
    }

    private double arredondar(double valor) {
        return Math.round(valor * 10) / 10.0;
    }
}
