package br.com.fiap.vagazero.fila.infrastructure.web;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.fiap.vagazero.fila.application.CascataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "4 - Cascata de vagas", description = "Ciclo de vida do convite: aceite, recusa e consulta do estado da cascata. Consulte 'Consultar estado da cascata' para descobrir o id do convite ativo antes de aceitar/recusar.")
@RestController
@RequestMapping("/convites")
public class ConviteController {

    private final CascataService cascataService;

    public ConviteController(CascataService cascataService) {
        this.cascataService = cascataService;
    }

    @Operation(summary = "Aceitar convite da cascata",
            description = "Ocupa a vaga, cria o agendamento confirmado, remove o paciente da fila e "
                    + "descarta os demais candidatos. Use o id do convite ENVIADO retornado por "
                    + "'Consultar estado da cascata'.")
    @ApiResponse(responseCode = "200", description = "Convite aceito, vaga ocupada")
    @ApiResponse(responseCode = "404", description = "Convite nao encontrado")
    @ApiResponse(responseCode = "409", description = "Convite ja nao esta mais disponivel (expirado, ja "
            + "tratado, ou a vaga nao pode mais ser ocupada)")
    @PostMapping("/{id}/aceitar")
    public ResultadoAceiteResponse aceitar(@Parameter(example = "1") @PathVariable Long id) {
        return ResultadoAceiteResponse.de(cascataService.aceitar(id));
    }

    @Operation(summary = "Recusar convite da cascata",
            description = "Marca o convite como recusado e convida imediatamente o proximo candidato "
                    + "elegivel da fila, sem esperar o TTL.")
    @ApiResponse(responseCode = "200", description = "Convite recusado, proximo candidato convidado (se houver)")
    @ApiResponse(responseCode = "404", description = "Convite nao encontrado")
    @ApiResponse(responseCode = "409", description = "Convite ja nao esta mais disponivel")
    @PostMapping("/{id}/recusar")
    public void recusar(@Parameter(example = "1") @PathVariable Long id) {
        cascataService.recusar(id);
    }
}
