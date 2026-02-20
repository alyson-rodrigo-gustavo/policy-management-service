package br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence;

import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyByIdNotFoundException;
import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyNotFoundException;
import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import br.com.alysongustavo.policymanagementservice.domain.port.outbound.PolicyRepositoryPort;
import br.com.alysongustavo.policymanagementservice.domain.port.outbound.PolicyTypeRepositoryPort;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyEntity;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyTypeEntity;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.mapper.PolicyJpaMapper;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.mapper.PolicyTypeJpaMapper;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.repository.PolicyJpaRepository;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.repository.PolicyTypeJpaRepository;
import br.com.alysongustavo.policymanagementservice.infra.cache.PolicyTypeCacheService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class PolicyTypeRepositoryAdapter implements PolicyTypeRepositoryPort {

    private final PolicyTypeCacheService policyTypeCacheService;
    private final PolicyTypeJpaMapper policyTypeJpaMapper;


    @Override
    public Optional<PolicyType> findById(Long id) {
        if (id == null) return Optional.empty();

        // Pega a lista O(1) do Redis e filtra na memória da app O(n) - como n é 5, a performance é máxima
        return policyTypeCacheService.getAll().stream()
                .filter(entity -> entity.getId().equals(id))
                .findFirst()
                .map(policyTypeJpaMapper::toDomain);
    }

    @Override
    public List<PolicyType> findAll() {
        return policyTypeCacheService.getAll().stream()
                .map(policyTypeJpaMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        if (id == null) return false;

        return policyTypeCacheService.getAll().stream()
                .anyMatch(entity -> entity.getId().equals(id));
    }
}
