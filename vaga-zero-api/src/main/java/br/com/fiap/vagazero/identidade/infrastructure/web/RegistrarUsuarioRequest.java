package br.com.fiap.vagazero.identidade.infrastructure.web;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.fiap.vagazero.identidade.domain.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

/**
 * Corpo unico para os dois perfis de registro. Nao ha campo de referencia a
 * um paciente existente: para PACIENTE, o registro do paciente e sempre
 * criado junto (nome/cns/telefone/dataNascimento/latitude/longitude), nunca
 * vinculado por id - isso elimina a classe de bug em que um id de paciente
 * inexistente estoura em violacao de FK. A coerencia entre perfil e dados
 * clinicos e garantida por @DadosClinicosCoerentesComPerfil.
 */
@DadosClinicosCoerentesComPerfil
public record RegistrarUsuarioRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String senha,
        @NotNull Perfil perfil,
        String nome,
        @Size(min = 15, max = 15) String cns,
        String telefone,
        @Past LocalDate dataNascimento,
        BigDecimal latitude,
        BigDecimal longitude) {
}
