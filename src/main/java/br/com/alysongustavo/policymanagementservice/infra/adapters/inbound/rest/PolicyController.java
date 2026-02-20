package br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest;

import br.com.alysongustavo.policymanagementservice.application.usecase.*;
import br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.dto.request.RegisterPolicyRequest;
import br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.dto.response.PolicyResponse;
import br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.mapper.PolicyRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/policies")
@AllArgsConstructor
public class PolicyController {

    private final ListPolicyUseCase listPolicyUseCase;
    private final RegisterPolicyUseCase registerPolicyUseCase;
    private final FindByIdPolicyUseCase findByIdPolicyUseCase;
    private final DeletePolicyUseCase deletePolicyUseCase;
    private final EditPolicyUseCase editPolicyUseCase;
    private final PolicyRestMapper policyRestMapper;

    @Operation(summary = "Busca uma apólice por ID", description = "Retorna os detalhes de uma apólice específica. Requer permissão de MANAGER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Apólice encontrada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (Requer role MANAGER)"),
            @ApiResponse(responseCode = "404", description = "Apólice não encontrada")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<PolicyResponse> getPolicyById(@PathVariable Long id) {
        PolicyResponse resp = policyRestMapper.toPolicyResponse(this.findByIdPolicyUseCase.execute(id));
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Lista todas as apólices", description = "Retorna uma lista contendo todas as apólices registradas. Requer permissão de MANAGER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listagem retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (Requer role MANAGER)")
    })
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<PolicyResponse>> listPolicies() {
        List<PolicyResponse> resp = this.listPolicyUseCase.execute()
                .stream().map(policyRestMapper::toPolicyResponse)
                .toList();

        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Registra uma nova apólice", description = "Cria uma nova apólice após validar as regras de cobertura de prêmio. Requer permissão de ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Apólice registrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos do payload (ex: valores negativos)"),
            @ApiResponse(responseCode = "401", description = "Não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (Requer role ADMIN)"),
            @ApiResponse(responseCode = "422", description = "Regra de negócio violada (Cobertura insuficiente ou Tipo de Apólice inexistente)")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PolicyResponse> registerPolicy(@RequestBody @Valid RegisterPolicyRequest registerPolicyRequest) {
        log.info("Recebendo requisição para criar apólice. Tipo: {},  Documento: {}, Valor: {}", registerPolicyRequest.getPolicyType(), registerPolicyRequest.getDocument(), registerPolicyRequest.getCoverageValue());

        var result = this.registerPolicyUseCase.execute(policyRestMapper.toCreatePolicyCommand(registerPolicyRequest));

        log.info("Apólice criada com sucesso. ID gerado: {}", result.id());
        return ResponseEntity.status(HttpStatus.OK).body(policyRestMapper.toPolicyResponse(result));
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Apólice atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos do payload"),
            @ApiResponse(responseCode = "401", description = "Não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (Requer role ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Apólice não encontrada para o ID informado"),
            @ApiResponse(responseCode = "422", description = "Regra de negócio violada (Cobertura insuficiente ou Tipo de Apólice inexistente)")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PolicyResponse> editPolicy(@RequestBody @Valid RegisterPolicyRequest registerPolicyRequest, @PathVariable Long id) {
        var result = this.editPolicyUseCase.execute(policyRestMapper.toCreatePolicyCommand(registerPolicyRequest), id);
        return ResponseEntity.status(HttpStatus.OK).body(policyRestMapper.toPolicyResponse(result));
    }

    @Operation(summary = "Deleta uma apólice", description = "Remove uma apólice do sistema pelo seu ID. Requer permissão de ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Apólice deletada com sucesso (Sem conteúdo no retorno)"),
            @ApiResponse(responseCode = "401", description = "Não autenticado (Token ausente ou inválido)"),
            @ApiResponse(responseCode = "403", description = "Sem permissão (Requer role ADMIN)"),
            @ApiResponse(responseCode = "404", description = "Apólice não encontrada")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePolicy(@PathVariable Long id) {
        deletePolicyUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

}
