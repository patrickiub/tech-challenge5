package br.com.fiap.vagazero.identidade.infrastructure.seguranca;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.fiap.vagazero.identidade.domain.CodificadorSenha;

@Component
public class CodificadorSenhaBCrypt implements CodificadorSenha {

    private final PasswordEncoder passwordEncoder;

    public CodificadorSenhaBCrypt(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String codificar(String senhaPura) {
        return passwordEncoder.encode(senhaPura);
    }

    @Override
    public boolean confere(String senhaPura, String hash) {
        return passwordEncoder.matches(senhaPura, hash);
    }
}
