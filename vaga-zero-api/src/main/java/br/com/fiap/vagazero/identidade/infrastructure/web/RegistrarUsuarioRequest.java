package br.com.fiap.vagazero.identidade.infrastructure.web;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.fiap.vagazero.identidade.domain.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @NotBlank @Email @Schema(example = "ana.costa@email.com") String email,
        @NotBlank @Size(min = 8, max = 100) @Schema(example = "ana12345") String senha,
        @NotNull @Schema(example = "PACIENTE", description = "GESTOR nao deve preencher os campos abaixo") Perfil perfil,
        @Schema(example = "Ana Costa", description = "Obrigatorio para perfil PACIENTE") String nome,
        @Size(min = 15, max = 15)
        @Schema(example = "700000000000029", description = "CNS (15 digitos) - obrigatorio para perfil PACIENTE")
        String cns,
        @Schema(example = "11998877665") String telefone,
        @Past
        @Schema(example = "1998-07-22", description = "Obrigatorio para perfil PACIENTE")
        LocalDate dataNascimento,
        @Schema(example = "-23.550520", description = "Obrigatorio para perfil PACIENTE") BigDecimal latitude,
        @Schema(example = "-46.633308", description = "Obrigatorio para perfil PACIENTE") BigDecimal longitude) {
}
