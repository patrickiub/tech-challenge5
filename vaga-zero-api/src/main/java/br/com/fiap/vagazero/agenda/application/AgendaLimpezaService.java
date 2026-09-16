package br.com.fiap.vagazero.agenda.application;

import org.springframework.stereotype.Service;

import br.com.fiap.vagazero.agenda.domain.PacienteRepositorio;

@Service
public class AgendaLimpezaService implements LimparAgendaUseCase {

    private final PacienteRepositorio pacienteRepositorio;

    public AgendaLimpezaService(PacienteRepositorio pacienteRepositorio) {
        this.pacienteRepositorio = pacienteRepositorio;
    }

    @Override
    public void limparTudo(String cnsPacienteFixo) {
        pacienteRepositorio.excluirTodosExceto(cnsPacienteFixo);
    }
}
