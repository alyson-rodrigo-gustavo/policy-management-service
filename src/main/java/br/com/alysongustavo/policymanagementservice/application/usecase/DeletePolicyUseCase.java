package br.com.alysongustavo.policymanagementservice.application.usecase;

import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyByIdNotFoundException;
import br.com.alysongustavo.policymanagementservice.domain.port.outbound.PolicyRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DeletePolicyUseCase {

    private final PolicyRepositoryPort policyRepositoryPort;

    public void execute(Long id) {
        this.policyRepositoryPort.findById(id)
                .orElseThrow(() -> new PolicyByIdNotFoundException(id));

        this.policyRepositoryPort.delete(id);
    }
}
