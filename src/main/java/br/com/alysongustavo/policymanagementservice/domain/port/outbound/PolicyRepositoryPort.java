package br.com.alysongustavo.policymanagementservice.domain.port.outbound;

import br.com.alysongustavo.policymanagementservice.domain.model.Policy;

import java.util.List;
import java.util.Optional;

public interface PolicyRepositoryPort {

    Policy save(Policy policy);
    Optional<Policy> findById(Long id);
    List<Policy> findAll();
    Optional<Policy> findByDocument(String document);
    Policy update(Policy policy, Long id);
    void delete(Long id);
    boolean existsByDocument(String document);
}
