package br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.mapper;

import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyEntity;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PolicyJpaMapper {

    PolicyEntity toEntity(Policy policy);
    Policy toDomain(PolicyEntity entity);

    default PolicyTypeEntity map(PolicyType policyType) {
        if (policyType == null || policyType.getId() == null) return null;
        PolicyTypeEntity e = new PolicyTypeEntity();
        e.setId(policyType.getId());
        return e;
    }

    default PolicyType map(PolicyTypeEntity entity) {
        if (entity == null || entity.getId() == null) return null;
        PolicyType pt = new PolicyType();
        pt.setId(entity.getId());
        pt.setName(entity.getName());
        return pt;
    }
}
