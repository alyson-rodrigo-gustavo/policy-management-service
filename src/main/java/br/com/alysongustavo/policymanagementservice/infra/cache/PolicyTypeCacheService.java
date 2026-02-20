package br.com.alysongustavo.policymanagementservice.infra.cache;

import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyTypeEntity;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.repository.PolicyTypeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PolicyTypeCacheService {

    public static final String CACHE_NAME = "policyTypes";

    private final PolicyTypeJpaRepository repository;

    /**
     * Carrega tudo do banco e armazena no Redis.
     * Chave fixa 'all' porque é reference data.
     */
    @Cacheable(cacheNames = CACHE_NAME, key = "'all'")
    public List<PolicyTypeEntity> getAll() {
        return repository.findAll();
    }

    /**
     * Opcional: mapa por name (facilita validações/lookup).
     * Também cacheado com chave fixa.
     */
    @Cacheable(cacheNames = CACHE_NAME, key = "'byName'")
    public Map<String, PolicyTypeEntity> getAllByName() {
        return repository.findAll().stream()
                .collect(Collectors.toMap(
                        PolicyTypeEntity::getName,
                        Function.identity()
                ));
    }

}
