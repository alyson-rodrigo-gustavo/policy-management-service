package br.com.alysongustavo.policymanagementservice.application.mapper;

import br.com.alysongustavo.policymanagementservice.application.usecase.input.CreatePolicyCommand;
import br.com.alysongustavo.policymanagementservice.application.usecase.output.RegisterPolicyResult;
import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PolicyMapper {

    Policy toDomain(CreatePolicyCommand command);

    @Mapping(target = "policyType", expression = "java(policy.getPolicyType() != null ? policy.getPolicyType().getId() : null)")
    RegisterPolicyResult toRegisterPolicyResult(Policy policy);

    default PolicyType mapToPolicyType(Long id) {
        if (id == null) return null;
        // Em vez de setId, use o construtor.
        // Se tiver 2 campos na classe, passe null no segundo.
        return new PolicyType(id, null);
    }

}
