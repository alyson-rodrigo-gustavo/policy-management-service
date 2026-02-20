package br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.repository;

import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PolicyJpaRepository extends JpaRepository<PolicyEntity, Long> {

    Optional<PolicyEntity> findByDocument(String document);
}