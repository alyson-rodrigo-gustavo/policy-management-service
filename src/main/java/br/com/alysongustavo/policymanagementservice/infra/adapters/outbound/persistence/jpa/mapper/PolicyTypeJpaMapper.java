package br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.mapper;

import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyEntity;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PolicyTypeJpaMapper {

    PolicyTypeEntity toEntity(PolicyType policyType);
    PolicyType toDomain(PolicyTypeEntity entity);

}
