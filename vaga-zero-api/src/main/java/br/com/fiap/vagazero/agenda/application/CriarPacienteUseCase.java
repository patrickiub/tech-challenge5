package br.com.fiap.vagazero.agenda.application;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.fiap.vagazero.agenda.domain.Paciente;

/**
 * Porto usado pelo modulo identidade para criar o registro de paciente no
 * momento do auto-cadastro, sem acessar o repositorio de agenda diretamente.
 */
public interface CriarPacienteUseCase {

    Paciente criar(
            String nome, String cns, String telefone, BigDecimal latitude, BigDecimal longitude,
            LocalDate dataNascimento);
}
