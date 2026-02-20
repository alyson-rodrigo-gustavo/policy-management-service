package br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.mapper;

import br.com.alysongustavo.policymanagementservice.application.usecase.input.CreatePolicyCommand;
import br.com.alysongustavo.policymanagementservice.application.usecase.output.RegisterPolicyResult;
import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.dto.request.RegisterPolicyRequest;
import br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.dto.response.PolicyResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PolicyRestMapper {

    PolicyResponse toPolicyResponse(Policy policy);

    CreatePolicyCommand toCreatePolicyCommand(RegisterPolicyRequest request);

    PolicyResponse toPolicyResponse(RegisterPolicyResult result);

    default PolicyType map(Long policyType) {
        return policyType == null ? null : new PolicyType(policyType, null);
    }

    default Long map(PolicyType policyType) {
        return (policyType == null) ? null : policyType.getId();
    }

}
