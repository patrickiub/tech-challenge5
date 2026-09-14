package br.com.fiap.vagazero.risco.domain;

/**
 * Pesos e limiares do motor de scoring de risco de falta. Configuraveis via
 * application.yml (vagazero.scoring.*), sem recompilar.
 */
public record PesosScoring(
        int pontosPorFalta,
        int tetoPontosFaltas,
        double distanciaLimiteKm,
        int pontosDistanciaAcimaDoLimite,
        int antecedenciaLimiteDias,
        int pontosAntecedenciaAcimaDoLimite,
        int pontosPrimeiraConsultaEspecialidade,
        int idadeMinima,
        int idadeMaxima,
        int pontosFaixaEtaria,
        int pontosConfirmacaoPresenca,
        int limiteBaixoMedio,
        int limiteMedioAlto) {
}
