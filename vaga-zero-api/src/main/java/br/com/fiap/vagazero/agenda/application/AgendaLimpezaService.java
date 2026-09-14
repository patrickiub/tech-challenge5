package br.com.fiap.vagazero.agenda.application;

import org.springframework.stereotype.Service;

import br.com.fiap.vagazero.agenda.domain.AgendamentoRepositorio;
import br.com.fiap.vagazero.agenda.domain.PacienteRepositorio;
import br.com.fiap.vagazero.agenda.domain.UnidadeRepositorio;
import br.com.fiap.vagazero.agenda.domain.VagaRepositorio;

@Service
public class AgendaLimpezaService implements LimparAgendaUseCase {

    private final AgendamentoRepositorio agendamentoRepositorio;
    private final VagaRepositorio vagaRepositorio;
    private final UnidadeRepositorio unidadeRepositorio;
    private final PacienteRepositorio pacienteRepositorio;

    public AgendaLimpezaService(
            AgendamentoRepositorio agendamentoRepositorio, VagaRepositorio vagaRepositorio,
            UnidadeRepositorio unidadeRepositorio, PacienteRepositorio pacienteRepositorio) {
        this.agendamentoRepositorio = agendamentoRepositorio;
        this.vagaRepositorio = vagaRepositorio;
        this.unidadeRepositorio = unidadeRepositorio;
        this.pacienteRepositorio = pacienteRepositorio;
    }

    @Override
    public void limparTudo(String cnsPacienteFixo) {
        agendamentoRepositorio.excluirTudo();
        vagaRepositorio.excluirTudo();
        unidadeRepositorio.excluirTudo();
        pacienteRepositorio.excluirTodosExceto(cnsPacienteFixo);
    }
}
