package br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence;

import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyByIdNotFoundException;
import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyNotFoundException;
import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.port.outbound.PolicyRepositoryPort;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyEntity;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.mapper.PolicyJpaMapper;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.repository.PolicyJpaRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class PolicyRepositoryAdapter  implements PolicyRepositoryPort {

    private final PolicyJpaRepository policyJpaRepository;
    private final PolicyJpaMapper policyJpaMapper;

    @Override
    public Policy save(Policy policy) {
        PolicyEntity policyEntity = policyJpaRepository.save(policyJpaMapper.toEntity(policy));
        return policyJpaMapper.toDomain(policyEntity);
    }

    @Override
    public Optional<Policy> findById(Long id) {
        return policyJpaRepository.findById(id)
                .map(policyJpaMapper::toDomain);
    }

    @Override
    public List<Policy> findAll() {
        return policyJpaRepository.findAll()
                .stream()
                .map(policyJpaMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Policy> findByDocument(String document) {
        return policyJpaRepository.findByDocument(document)
                .map(policyJpaMapper::toDomain);
    }

    @Override
    public Policy update(Policy policy, Long id) {
        PolicyEntity current = policyJpaRepository.findById(id)
                .orElseThrow(() -> new PolicyByIdNotFoundException(id));

        BeanUtils.copyProperties(policy, current, "id");
        PolicyEntity saved = policyJpaRepository.save(current);
        return policyJpaMapper.toDomain(saved);
    }

    @Override
    public void delete(Long id) {
        PolicyEntity entity = policyJpaRepository.findById(id)
                .orElseThrow(() -> new PolicyNotFoundException(String.format("Policy with id %d not found", id)));

        policyJpaRepository.delete(entity);
    }

    @Override
    public boolean existsByDocument(String document) {
        return policyJpaRepository.findByDocument(document).isPresent();
    }
}
