package br.com.fiap.vagazero.fila.infrastructure.persistencia;

import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.fiap.vagazero.fila.domain.AvisoPendente;
import br.com.fiap.vagazero.fila.domain.AvisoPendenteRepositorio;
import br.com.fiap.vagazero.fila.domain.StatusAviso;

@Repository
public class AvisoPendenteRepositorioAdapter implements AvisoPendenteRepositorio {

    private final AvisoPendenteJpaRepository jpaRepository;

    public AvisoPendenteRepositorioAdapter(AvisoPendenteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public AvisoPendente salvar(AvisoPendente aviso) {
        AvisoPendenteJpaEntity entidade = new AvisoPendenteJpaEntity(
                aviso.id(), aviso.conviteId(), aviso.destinatario(), aviso.canal(), aviso.mensagem(),
                aviso.status(), aviso.tentativas(), aviso.criadoEm(), aviso.ultimaTentativaEm());
        return paraDomain(jpaRepository.save(entidade));
    }

    @Override
    public List<AvisoPendente> listarPendentes() {
        return jpaRepository.findByStatus(StatusAviso.PENDENTE).stream().map(this::paraDomain).toList();
    }

    private AvisoPendente paraDomain(AvisoPendenteJpaEntity entidade) {
        return new AvisoPendente(
                entidade.getId(), entidade.getConviteId(), entidade.getDestinatario(), entidade.getCanal(),
                entidade.getMensagem(), entidade.getStatus(), entidade.getTentativas(), entidade.getCriadoEm(),
                entidade.getUltimaTentativaEm());
    }
}
