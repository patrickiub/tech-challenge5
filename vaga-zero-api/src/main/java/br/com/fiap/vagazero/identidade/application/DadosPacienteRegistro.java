package br.com.fiap.vagazero.identidade.application;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dados clinicos minimos exigidos para criar o paciente no auto-cadastro de
 * um usuario com perfil PACIENTE. Nulo quando o perfil e GESTOR.
 */
public record DadosPacienteRegistro(
        String nome, String cns, String telefone, LocalDate dataNascimento, BigDecimal latitude,
        BigDecimal longitude) {
}
