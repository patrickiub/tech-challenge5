package br.com.fiap.vagazero.identidade.infrastructure.seguranca;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Nao e' um @Component: e' instanciado manualmente em SecurityConfig para nao
 * ser tambem auto-registrado como filtro generico de servlet pelo Spring Boot,
 * o que duplicaria sua execucao e limparia a autenticacao antes da checagem
 * de autorizacao do Spring Security.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String cabecalho = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecalho != null && cabecalho.startsWith(PREFIXO_BEARER)) {
            String token = cabecalho.substring(PREFIXO_BEARER.length());
            jwtService.validar(token).ifPresent(claims -> {
                String email = claims.getSubject();
                String perfil = claims.get("perfil", String.class);
                var autenticacao = new UsernamePasswordAuthenticationToken(
                        email, null, List.of(new SimpleGrantedAuthority(perfil)));
                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            });
        }
        filterChain.doFilter(request, response);
    }
}
