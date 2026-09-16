package br.com.fiap.vagazero.demo.application;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.fiap.vagazero.agenda.application.AgendamentoService;
import br.com.fiap.vagazero.agenda.application.LimparAgendaUseCase;
import br.com.fiap.vagazero.agenda.application.PacienteService;
import br.com.fiap.vagazero.agenda.application.UnidadeService;
import br.com.fiap.vagazero.agenda.application.VagaService;
import br.com.fiap.vagazero.agenda.domain.Agendamento;
import br.com.fiap.vagazero.agenda.domain.Paciente;
import br.com.fiap.vagazero.agenda.domain.StatusAgendamento;
import br.com.fiap.vagazero.agenda.domain.Unidade;
import br.com.fiap.vagazero.agenda.domain.Vaga;
import br.com.fiap.vagazero.demo.infrastructure.RelogioAjustavel;
import br.com.fiap.vagazero.demo.infrastructure.persistencia.TruncadorDeTabelas;
import br.com.fiap.vagazero.fila.application.FilaService;

/**
 * Popula o cenario de demonstracao. Reusa os servicos de aplicacao de cada
 * modulo (nunca acessa repositorio de outro modulo diretamente), igual a
 * qualquer outro consumidor cross-modulo do sistema.
 */
@Service
public class SeedService {

    private static final String ESPECIALIDADE = "Oftalmologia";
    private static final String CNS_PACIENTE_FIXO_MIGRATION = "700003054441234";
    private static final Set<String> TABELAS_PRESERVADAS_NO_RESET =
            Set.of("usuario", "paciente", "flyway_schema_history");
    private static final BigDecimal UNIDADE_LATITUDE = new BigDecimal("-23.589000");
    private static final BigDecimal UNIDADE_LONGITUDE = new BigDecimal("-46.642000");

    /**
     * Tabela aprovada dos 30 pacientes da fila: nome, prioridade clinica
     * (1-5), dias atras da entrada na fila, se aceita chamado imediato,
     * distancia ate a unidade em km e raio maximo aceito em km.
     */
    private static final List<PerfilFila> PERFIS_FILA = List.of(
            new PerfilFila("Ana Beatriz Ramos", 5, 120, true, 3, 30),
            new PerfilFila("Marcos Vinicius Teixeira", 5, 90, true, 25, 10),
            new PerfilFila("Juliana Prado Costa", 5, 60, false, 4, 40),
            new PerfilFila("Eduardo Nascimento Alves", 5, 45, true, 6, 20),
            new PerfilFila("Camila Rocha Ferreira", 4, 150, true, 2, 15),
            new PerfilFila("Bruno Cardoso Lima", 4, 100, true, 12, 15),
            new PerfilFila("Patricia Gomes Duarte", 4, 80, true, 18, 10),
            new PerfilFila("Rafael Souza Martins", 4, 70, false, 5, 25),
            new PerfilFila("Larissa Andrade Pinto", 4, 40, true, 9, 20),
            new PerfilFila("Felipe Barbosa Ribeiro", 3, 200, true, 7, 15),
            new PerfilFila("Gabriela Monteiro Silva", 3, 170, true, 33, 40),
            new PerfilFila("Thiago Correia Dias", 3, 130, true, 15, 10),
            new PerfilFila("Vanessa Moraes Castro", 3, 110, false, 3, 30),
            new PerfilFila("Diego Fernandes Rocha", 3, 95, true, 22, 25),
            new PerfilFila("Renata Cavalcante Sales", 3, 60, true, 4, 12),
            new PerfilFila("Andre Luiz Mendes", 3, 50, true, 38, 15),
            new PerfilFila("Isabela Cunha Farias", 3, 30, true, 10, 20),
            new PerfilFila("Lucas Pereira Goncalves", 2, 220, true, 6, 10),
            new PerfilFila("Fernanda Azevedo Reis", 2, 190, true, 28, 50),
            new PerfilFila("Gustavo Henrique Braga", 2, 160, false, 2, 20),
            new PerfilFila("Beatriz Xavier Nogueira", 2, 140, true, 14, 10),
            new PerfilFila("Rodrigo Marques Vieira", 2, 100, true, 8, 30),
            new PerfilFila("Carolina Nunes Pires", 2, 85, true, 20, 25),
            new PerfilFila("Vinicius Campos Teles", 2, 55, true, 35, 20),
            new PerfilFila("Aline Barros Freitas", 2, 20, true, 5, 15),
            new PerfilFila("Paulo Ricardo Machado", 1, 250, true, 9, 20),
            new PerfilFila("Debora Lopes Amaral", 1, 210, true, 30, 40),
            new PerfilFila("Marcelo Tavares Rezende", 1, 180, false, 3, 25),
            new PerfilFila("Simone Batista Cordeiro", 1, 150, true, 16, 12),
            new PerfilFila("Henrique Soares Peixoto", 1, 90, true, 11, 15));

    private final UnidadeService unidadeService;
    private final PacienteService pacienteService;
    private final VagaService vagaService;
    private final AgendamentoService agendamentoService;
    private final FilaService filaService;
    private final LimparAgendaUseCase limparAgendaUseCase;
    private final TruncadorDeTabelas truncadorDeTabelas;
    private final RelogioAjustavel relogio;

    public SeedService(
            UnidadeService unidadeService, PacienteService pacienteService, VagaService vagaService,
            AgendamentoService agendamentoService, FilaService filaService,
            LimparAgendaUseCase limparAgendaUseCase, TruncadorDeTabelas truncadorDeTabelas,
            RelogioAjustavel relogio) {
        this.unidadeService = unidadeService;
        this.pacienteService = pacienteService;
        this.vagaService = vagaService;
        this.agendamentoService = agendamentoService;
        this.filaService = filaService;
        this.limparAgendaUseCase = limparAgendaUseCase;
        this.truncadorDeTabelas = truncadorDeTabelas;
        this.relogio = relogio;
    }

    public SeedResultado semear() {
        LocalDateTime agora = LocalDateTime.now(relogio);
        Unidade unidade = unidadeService.criar("UBS Vila Mariana", UNIDADE_LATITUDE, UNIDADE_LONGITUDE);

        for (int i = 0; i < PERFIS_FILA.size(); i++) {
            PerfilFila perfil = PERFIS_FILA.get(i);
            Paciente paciente = criarPaciente(
                    perfil.nome(), "80000" + String.format("%04d", i + 1), perfil.distanciaKm(),
                    LocalDate.of(1970 + (i % 40), 1 + (i % 12), 1 + (i % 28)));
            filaService.entrar(
                    paciente.id(), ESPECIALIDADE, perfil.prioridade(), perfil.aceitaChamadoImediato(),
                    BigDecimal.valueOf(perfil.raioMaximoKm()), agora.minusDays(perfil.diasNaFilaAtras()));
        }

        Paciente roberto = criarPaciente("Roberto Nunes", "90000001", 5, LocalDate.of(1985, 4, 12));
        Vaga vagaRoberto = vagaService.criar(unidade.id(), ESPECIALIDADE, "Dra. Beatriz Lima", agora.plusDays(10));
        Agendamento agendamentoRoberto = agendamentoService.criar(vagaRoberto.id(), roberto.id());

        Paciente marisa = criarPaciente("Marisa Aparecida Guedes", "90000002", 22, LocalDate.of(1981, 8, 3));
        criarFaltaPassada(unidade.id(), marisa.id(), agora.minusDays(200));
        criarFaltaPassada(unidade.id(), marisa.id(), agora.minusDays(60));
        Vaga vagaMarisa = vagaService.criar(unidade.id(), ESPECIALIDADE, "Dr. Henrique Cardoso", agora.plusDays(2));
        Agendamento agendamentoMarisa = agendamentoService.criar(vagaMarisa.id(), marisa.id());

        criarVagaDisponivel(unidade.id(), "Dra. Camila Nogueira", agora.plusDays(5));
        criarVagaDisponivel(unidade.id(), "Dra. Beatriz Lima", agora.plusDays(7));
        criarVagaDisponivel(unidade.id(), "Dr. Henrique Cardoso", agora.plusDays(12));
        criarVagaDisponivel(unidade.id(), "Dra. Camila Nogueira", agora.plusDays(15));
        criarVagaDisponivel(unidade.id(), "Dra. Beatriz Lima", agora.plusDays(18));
        criarVagaDisponivel(unidade.id(), "Dr. Henrique Cardoso", agora.plusDays(20));

        return new SeedResultado(
                unidade.id(), 10, PERFIS_FILA.size(),
                vagaRoberto.id(), agendamentoRoberto.id(), roberto.id(),
                "Caminho 1 - cascata manual: POST /agendamentos/" + agendamentoRoberto.id() + "/cancelar libera "
                        + "a vaga de Roberto Nunes e inicia a cascata imediatamente. Acompanhe com GET "
                        + "/vagas/" + vagaRoberto.id() + "/cascata.",
                vagaMarisa.id(), agendamentoMarisa.id(), marisa.id(),
                "Caminho 2 - job D-2/D-1: a vaga de Marisa Aparecida Guedes esta a 2 dias (job avalia "
                        + "automaticamente em poucos segundos e classifica ALTO: 2 faltas nos ultimos 12 meses "
                        + "+50, distancia 22km>10km +15 = 65). Chame POST /demo/avancar-dias/1 (exatamente 1 - "
                        + "mais que isso a vaga passa da data e vira PERDIDA em vez de cascata) para diasAte=1: "
                        + "o job libera a vaga preventivamente e inicia a cascata. Confira com GET "
                        + "/agendamentos/" + agendamentoMarisa.id() + "/risco e GET /vagas/" + vagaMarisa.id()
                        + "/cascata.",
                "Os dois caminhos usam a mesma fila de Oftalmologia e o mesmo relogio: avancar o relogio expira "
                        + "qualquer convite ativo do outro caminho (o TTL usa o mesmo Clock). Rode um caminho, "
                        + "chame POST /demo/reset, e so entao rode o outro - nao demonstre os dois na mesma "
                        + "execucao.");
    }

    public LocalDateTime avancarDias(int dias) {
        relogio.avancar(Duration.ofDays(dias));
        return LocalDateTime.now(relogio);
    }

    @Transactional
    public SeedResultado resetar() {
        truncadorDeTabelas.truncarTudoExceto(TABELAS_PRESERVADAS_NO_RESET);
        limparAgendaUseCase.limparTudo(CNS_PACIENTE_FIXO_MIGRATION);
        relogio.resetar();
        return semear();
    }

    private Paciente criarPaciente(String nome, String cnsBase, double distanciaKm, LocalDate dataNascimento) {
        BigDecimal latitude = BigDecimal.valueOf(UNIDADE_LATITUDE.doubleValue() - distanciaKm / 111.0);
        return pacienteService.criar(
                nome, cnsBase + "0000", "119" + cnsBase, latitude, UNIDADE_LONGITUDE, dataNascimento);
    }

    private void criarFaltaPassada(Long unidadeId, Long pacienteId, LocalDateTime dataHora) {
        Vaga vaga = vagaService.criar(unidadeId, ESPECIALIDADE, "Dra. Beatriz Lima", dataHora);
        Agendamento agendamento = agendamentoService.criar(vaga.id(), pacienteId);
        agendamentoService.atualizarStatus(agendamento.id(), StatusAgendamento.FALTOU, null);
    }

    private void criarVagaDisponivel(Long unidadeId, String profissional, LocalDateTime dataHora) {
        vagaService.criar(unidadeId, ESPECIALIDADE, profissional, dataHora);
    }

    private record PerfilFila(
            String nome, int prioridade, int diasNaFilaAtras, boolean aceitaChamadoImediato, double distanciaKm,
            double raioMaximoKm) {
    }
}
