package br.com.fiap.vagazero.identidade.infrastructure.web;

import br.com.fiap.vagazero.identidade.domain.Perfil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DadosClinicosCoerentesComPerfilValidator
        implements ConstraintValidator<DadosClinicosCoerentesComPerfil, RegistrarUsuarioRequest> {

    @Override
    public boolean isValid(RegistrarUsuarioRequest requisicao, ConstraintValidatorContext contexto) {
        if (requisicao == null || requisicao.perfil() == null) {
            return true;
        }

        boolean temAlgumDadoClinico = requisicao.nome() != null || requisicao.cns() != null
                || requisicao.telefone() != null || requisicao.dataNascimento() != null
                || requisicao.latitude() != null || requisicao.longitude() != null;

        if (requisicao.perfil() == Perfil.GESTOR) {
            if (temAlgumDadoClinico) {
                return falhar(contexto,
                        "Perfil GESTOR nao deve informar dados de paciente (nome, cns, telefone, "
                                + "dataNascimento, latitude, longitude)");
            }
            return true;
        }

        boolean completo = naoVazio(requisicao.nome()) && naoVazio(requisicao.cns())
                && requisicao.dataNascimento() != null
                && requisicao.latitude() != null
                && requisicao.longitude() != null;
        if (!completo) {
            return falhar(contexto,
                    "Perfil PACIENTE exige nome, cns, dataNascimento, latitude e longitude para criar "
                            + "o registro de paciente");
        }
        return true;
    }

    private boolean naoVazio(String valor) {
        return valor != null && !valor.isBlank();
    }

    private boolean falhar(ConstraintValidatorContext contexto, String mensagem) {
        contexto.disableDefaultConstraintViolation();
        contexto.buildConstraintViolationWithTemplate(mensagem).addConstraintViolation();
        return false;
    }
}
