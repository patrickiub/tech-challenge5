package br.com.fiap.vagazero.identidade.infrastructure.seguranca;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.fiap.vagazero.identidade.domain.GeradorTokenJwt;
import br.com.fiap.vagazero.identidade.domain.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService implements GeradorTokenJwt {

    private static final String CLAIM_PERFIL = "perfil";

    private final SecretKey chave;
    private final long expiracaoMinutos;
    private final Clock clock;

    public JwtService(
            @Value("${vagazero.jwt.secret}") String segredo,
            @Value("${vagazero.jwt.expiracao-minutos}") long expiracaoMinutos,
            Clock clock) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
        this.expiracaoMinutos = expiracaoMinutos;
        this.clock = clock;
    }

    @Override
    public String gerar(Usuario usuario) {
        Instant agora = clock.instant();
        return Jwts.builder()
                .subject(usuario.email())
                .claim(CLAIM_PERFIL, usuario.perfil().name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(Duration.ofMinutes(expiracaoMinutos))))
                .signWith(chave)
                .compact();
    }

    public Optional<Claims> validar(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .clock(() -> Date.from(clock.instant()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (JwtException | IllegalArgumentException excecao) {
            return Optional.empty();
        }
    }
}
