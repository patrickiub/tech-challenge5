package br.com.fiap.vagazero.identidade.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.vagazero.agenda.application.CriarPacienteUseCase;
import br.com.fiap.vagazero.agenda.domain.Paciente;
import br.com.fiap.vagazero.identidade.domain.CodificadorSenha;
import br.com.fiap.vagazero.identidade.domain.EmailJaCadastradoException;
import br.com.fiap.vagazero.identidade.domain.Perfil;
import br.com.fiap.vagazero.identidade.domain.Usuario;
import br.com.fiap.vagazero.identidade.domain.UsuarioRepositorio;

@Service
public class RegistrarUsuarioService implements RegistrarUsuarioUseCase {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CodificadorSenha codificadorSenha;
    private final CriarPacienteUseCase criarPacienteUseCase;

    public RegistrarUsuarioService(
            UsuarioRepositorio usuarioRepositorio, CodificadorSenha codificadorSenha,
            CriarPacienteUseCase criarPacienteUseCase) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.codificadorSenha = codificadorSenha;
        this.criarPacienteUseCase = criarPacienteUseCase;
    }

    /**
     * Cria o usuario e, para o perfil PACIENTE, o registro de paciente
     * correspondente na mesma transacao - o primeiro acesso nao depende de um
     * cadastro previo feito por um GESTOR.
     */
    @Override
    @Transactional
    public Usuario registrar(String email, String senha, Perfil perfil, DadosPacienteRegistro dadosPaciente) {
        if (usuarioRepositorio.existePorEmail(email)) {
            throw new EmailJaCadastradoException(email);
        }

        Long pacienteId = null;
        if (perfil == Perfil.PACIENTE) {
            Paciente paciente = criarPacienteUseCase.criar(
                    dadosPaciente.nome(), dadosPaciente.cns(), dadosPaciente.telefone(),
                    dadosPaciente.latitude(), dadosPaciente.longitude(), dadosPaciente.dataNascimento());
            pacienteId = paciente.id();
        }

        String senhaHash = codificadorSenha.codificar(senha);
        Usuario usuario = new Usuario(null, email, senhaHash, perfil, pacienteId);
        return usuarioRepositorio.salvar(usuario);
    }
}
