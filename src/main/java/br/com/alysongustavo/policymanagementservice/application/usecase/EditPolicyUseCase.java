package br.com.alysongustavo.policymanagementservice.application.usecase;

import br.com.alysongustavo.policymanagementservice.application.mapper.PolicyMapper;
import br.com.alysongustavo.policymanagementservice.application.usecase.input.CreatePolicyCommand;
import br.com.alysongustavo.policymanagementservice.application.usecase.output.RegisterPolicyResult;
import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyNotFoundException;
import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyTypeNotFoundException;
import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import br.com.alysongustavo.policymanagementservice.domain.port.outbound.PolicyRepositoryPort;
import br.com.alysongustavo.policymanagementservice.domain.port.outbound.PolicyTypeRepositoryPort;
import br.com.alysongustavo.policymanagementservice.domain.service.PolicyCoverageEligibilityRule;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EditPolicyUseCase {

    private final PolicyRepositoryPort policyRepositoryPort;

    private final PolicyTypeRepositoryPort policyTypeRepositoryPort;

    private final PolicyCoverageEligibilityRule validatePolicyCoverageEligibilityRule;

    private final PolicyMapper policyMapper;

    public RegisterPolicyResult execute(CreatePolicyCommand command, Long id) {

        validatePolicyCoverageEligibilityRule.validateCoverage(command.premiumValue(), command.coverageValue());

        if (!policyRepositoryPort.existsByDocument(command.document())) {
            throw new PolicyNotFoundException(command.document());
        }

        PolicyType policyType = policyTypeRepositoryPort.findById(command.policyType())
                .orElseThrow(() -> new PolicyTypeNotFoundException(command.policyType()));

        Policy policy = policyMapper.toDomain(command);
        Policy saved = policyRepositoryPort.update(policy, id);
        return policyMapper.toRegisterPolicyResult(saved);
    }
}
