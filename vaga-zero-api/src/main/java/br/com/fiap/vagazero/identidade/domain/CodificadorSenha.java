package br.com.fiap.vagazero.identidade.domain;

/**
 * Porta para hashing e verificacao de senha. Implementada na camada de
 * infraestrutura (BCrypt).
 */
public interface CodificadorSenha {

    String codificar(String senhaPura);

    boolean confere(String senhaPura, String hash);
}
