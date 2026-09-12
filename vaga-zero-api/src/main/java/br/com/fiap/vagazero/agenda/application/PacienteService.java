package br.com.fiap.vagazero.agenda.application;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.fiap.vagazero.agenda.domain.CnsJaCadastradoException;
import br.com.fiap.vagazero.agenda.domain.Paciente;
import br.com.fiap.vagazero.agenda.domain.PacienteNaoEncontradoException;
import br.com.fiap.vagazero.agenda.domain.PacienteRepositorio;

@Service
public class PacienteService {

    private final PacienteRepositorio pacienteRepositorio;

    public PacienteService(PacienteRepositorio pacienteRepositorio) {
        this.pacienteRepositorio = pacienteRepositorio;
    }

    public Paciente criar(
            String nome, String cns, String telefone, BigDecimal latitude, BigDecimal longitude,
            LocalDate dataNascimento) {
        if (pacienteRepositorio.existePorCns(cns)) {
            throw new CnsJaCadastradoException(cns);
        }
        Paciente paciente = new Paciente(null, nome, cns, telefone, latitude, longitude, dataNascimento);
        return pacienteRepositorio.salvar(paciente);
    }

    public Paciente buscarPorId(Long id) {
        return pacienteRepositorio.buscarPorId(id)
                .orElseThrow(() -> new PacienteNaoEncontradoException(id));
    }

    public List<Paciente> listarTodos() {
        return pacienteRepositorio.listarTodos();
    }

    public Paciente atualizar(
            Long id, String nome, String telefone, BigDecimal latitude, BigDecimal longitude,
            LocalDate dataNascimento) {
        Paciente existente = buscarPorId(id);
        Paciente atualizado = new Paciente(
                id, nome, existente.cns(), telefone, latitude, longitude, dataNascimento);
        return pacienteRepositorio.salvar(atualizado);
    }

    public void excluir(Long id) {
        buscarPorId(id);
        pacienteRepositorio.excluir(id);
    }
}
