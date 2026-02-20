package br.com.alysongustavo.policymanagementservice.application.usecase;

import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.port.outbound.PolicyRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ListPolicyUseCase {

    private final PolicyRepositoryPort policyRepositoryPort;

    public List<Policy> execute() {
        return policyRepositoryPort.findAll();
    }
}
