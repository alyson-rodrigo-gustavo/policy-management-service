package br.com.alysongustavo.policymanagementservice.domain.port.outbound;

import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;

import java.util.List;
import java.util.Optional;

public interface PolicyTypeRepositoryPort {

    Optional<PolicyType> findById(Long id);
    List<PolicyType> findAll();
    boolean existsById(Long id);
}
