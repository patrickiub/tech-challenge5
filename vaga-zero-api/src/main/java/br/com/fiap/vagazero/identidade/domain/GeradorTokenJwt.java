package br.com.fiap.vagazero.identidade.domain;

/**
 * Porta para geracao de token de autenticacao. Implementada na camada de
 * infraestrutura.
 */
public interface GeradorTokenJwt {

    String gerar(Usuario usuario);
}
