package br.com.alysongustavo.policymanagementservice.application.usecase;

import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyByIdNotFoundException;
import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.port.outbound.PolicyRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FindByIdPolicyUseCase {

    private final PolicyRepositoryPort policyRepositoryPort;

    public Policy execute(Long id) {
        return policyRepositoryPort.findById(id)
                .orElseThrow(() -> new PolicyByIdNotFoundException(id));
    }
}
