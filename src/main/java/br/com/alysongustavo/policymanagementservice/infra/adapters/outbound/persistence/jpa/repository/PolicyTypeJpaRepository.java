package br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.repository;

import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyTypeJpaRepository extends JpaRepository<PolicyTypeEntity, Long> {

    Optional<PolicyTypeEntity> findByName(String name);
}