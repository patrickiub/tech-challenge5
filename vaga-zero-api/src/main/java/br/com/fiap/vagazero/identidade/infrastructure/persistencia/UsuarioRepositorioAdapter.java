package br.com.fiap.vagazero.identidade.infrastructure.persistencia;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.fiap.vagazero.identidade.domain.Usuario;
import br.com.fiap.vagazero.identidade.domain.UsuarioRepositorio;

@Repository
public class UsuarioRepositorioAdapter implements UsuarioRepositorio {

    private final UsuarioJpaRepository jpaRepository;
    private final Clock clock;

    public UsuarioRepositorioAdapter(UsuarioJpaRepository jpaRepository, Clock clock) {
        this.jpaRepository = jpaRepository;
        this.clock = clock;
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        UsuarioJpaEntity entidade = new UsuarioJpaEntity(
                usuario.id(),
                usuario.email(),
                usuario.senhaHash(),
                usuario.perfil(),
                usuario.pacienteId(),
                LocalDateTime.now(clock));
        return paraDomain(jpaRepository.save(entidade));
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::paraDomain);
    }

    private Usuario paraDomain(UsuarioJpaEntity entidade) {
        return new Usuario(
                entidade.getId(),
                entidade.getEmail(),
                entidade.getSenhaHash(),
                entidade.getPerfil(),
                entidade.getPacienteId());
    }
}
