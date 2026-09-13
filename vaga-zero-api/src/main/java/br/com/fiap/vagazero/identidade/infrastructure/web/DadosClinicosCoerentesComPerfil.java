package br.com.fiap.vagazero.identidade.infrastructure.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Garante que os dados clinicos do RegistrarUsuarioRequest sao coerentes com
 * o perfil informado: GESTOR nao pode traze-los, PACIENTE precisa deles para
 * que o paciente seja criado junto com o usuario.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DadosClinicosCoerentesComPerfilValidator.class)
public @interface DadosClinicosCoerentesComPerfil {

    String message() default "Dados clinicos incoerentes com o perfil informado";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
