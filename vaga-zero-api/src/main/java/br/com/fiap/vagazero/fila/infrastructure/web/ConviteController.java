package br.com.fiap.vagazero.fila.infrastructure.web;

import java.time.Clock;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.agenda.application.ConsultaPacienteUseCase;
import br.com.fiap.vagazero.fila.application.CascataService;
import br.com.fiap.vagazero.fila.application.ResultadoAceite;
import br.com.fiap.vagazero.fila.application.ResultadoRecusa;
import br.com.fiap.vagazero.shared.kafka.KafkaTopics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "4 - Cascata de vagas", description = "Ciclo de vida do convite: aceite, recusa e consulta do estado da cascata. Consulte 'Consultar estado da cascata' para descobrir o id do convite ativo antes de aceitar/recusar.")
@RestController
@RequestMapping("/convites")
public class ConviteController {

    private final CascataService cascataService;
    private final ConsultaPacienteUseCase consultaPacienteUseCase;
    private final Clock clock;

    public ConviteController(
            CascataService cascataService, ConsultaPacienteUseCase consultaPacienteUseCase, Clock clock) {
        this.cascataService = cascataService;
        this.consultaPacienteUseCase = consultaPacienteUseCase;
        this.clock = clock;
    }

    @Operation(summary = "Aceitar convite da cascata",
            description = "Ocupa a vaga, cria o agendamento confirmado, remove o paciente da fila e "
                    + "descarta os demais candidatos. Use o id do convite ENVIADO retornado por "
                    + "'Consultar estado da cascata'. A resposta traz os eventos Kafka publicados e o "
                    + "estado resultante da cascata.")
    @ApiResponse(responseCode = "200", description = "Convite aceito, vaga ocupada")
    @ApiResponse(responseCode = "404", description = "Convite nao encontrado")
    @ApiResponse(responseCode = "409", description = "Convite ja nao esta mais disponivel (expirado, ja "
            + "tratado, ou a vaga nao pode mais ser ocupada)")
    @PostMapping("/{id}/aceitar")
    public AceiteConviteResponse aceitar(@Parameter(example = "1") @PathVariable Long id) {
        ResultadoAceite resultado = cascataService.aceitar(id);
        String nomePaciente = consultaPacienteUseCase.buscarResumo(resultado.pacienteId()).nome();
        CascataEstadoResponse estadoAtual = CascataEstadoResponse.de(
                cascataService.consultarEstado(resultado.vagaId()), clock);
        return new AceiteConviteResponse(
                id, resultado.vagaId(), resultado.agendamentoId(), resultado.pacienteId(), nomePaciente,
                List.of(KafkaTopics.CONVITE_ACEITO, KafkaTopics.VAGA_PREENCHIDA), estadoAtual);
    }

    @Operation(summary = "Recusar convite da cascata",
            description = "Marca o convite como recusado e convida imediatamente o proximo candidato "
                    + "elegivel da fila, sem esperar o TTL. A resposta traz o evento Kafka publicado e o "
                    + "estado resultante da cascata (proximo convidado, ou PERDIDA se a lista se esgotou).")
    @ApiResponse(responseCode = "200", description = "Convite recusado, proximo candidato convidado (se houver)")
    @ApiResponse(responseCode = "404", description = "Convite nao encontrado")
    @ApiResponse(responseCode = "409", description = "Convite ja nao esta mais disponivel")
    @PostMapping("/{id}/recusar")
    public RecusaConviteResponse recusar(@Parameter(example = "1") @PathVariable Long id) {
        ResultadoRecusa resultado = cascataService.recusar(id);
        String nomePaciente = consultaPacienteUseCase.buscarResumo(resultado.pacienteId()).nome();
        CascataEstadoResponse estadoAtual = CascataEstadoResponse.de(
                cascataService.consultarEstado(resultado.vagaId()), clock);
        return new RecusaConviteResponse(
                resultado.conviteId(), resultado.vagaId(), resultado.pacienteId(), nomePaciente,
                List.of(KafkaTopics.CONVITE_EXPIRADO), estadoAtual);
    }
}
