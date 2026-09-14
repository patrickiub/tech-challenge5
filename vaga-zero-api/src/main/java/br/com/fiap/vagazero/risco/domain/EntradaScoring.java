package br.com.fiap.vagazero.risco.domain;

/**
 * Dados brutos de um agendamento, ja apurados pela camada de aplicacao
 * (contagens, distancias, idade), que o motor compara contra os limiares de
 * PesosScoring. Mantem o motor puro e facil de testar com valores exatos nas
 * fronteiras de cada regra.
 */
public record EntradaScoring(
        int faltasUltimos12Meses,
        double distanciaKm,
        long diasAntecedencia,
        boolean primeiraConsultaNaEspecialidade,
        int idade,
        boolean confirmouPresenca) {
}
