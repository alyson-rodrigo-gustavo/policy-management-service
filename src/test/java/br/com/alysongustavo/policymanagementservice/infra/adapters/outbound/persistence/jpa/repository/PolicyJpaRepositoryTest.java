package br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.repository;

import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyEntity;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyTypeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class PolicyJpaRepositoryTest {

    @Autowired
    private PolicyJpaRepository policyJpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should return policy when searching by existing Document")
    void shouldReturnPolicyWhenSearchingByExistingDocument() {
        String document = "12345678900";

        PolicyTypeEntity policyType = new PolicyTypeEntity();
        policyType.setName("AUTO");
        entityManager.persist(policyType);

        PolicyEntity entity = criarPolicyEntity(document);
        entity.setPolicyType(policyType);

        entityManager.persist(entity);

        entityManager.flush();
        entityManager.clear();

        Optional<PolicyEntity> result = policyJpaRepository.findByDocument(document);

        assertThat(result).isPresent();
        assertThat(result.get().getDocument()).isEqualTo(document);
    }

    @Test
    @DisplayName("Should return empty when searching by non-existing DOCUMENT")
    void shouldReturnEmptyWhenSearchingByNonExistingDocument() {
        String document = "99999999999";

        Optional<PolicyEntity> result = policyJpaRepository.findByDocument(document);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should persist policy and generate id automatically")
    void shouldPersistPolicyAndGenerateIdAutomatically() {
        PolicyEntity entity = criarPolicyEntity("11122233344");

        PolicyEntity saved = policyJpaRepository.save(entity);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDocument()).isEqualTo("11122233344");
    }

    private PolicyEntity criarPolicyEntity(String document) {
        return new PolicyEntity(
                null,
                document,
                BigDecimal.TEN,
                BigDecimal.TEN,
                new PolicyTypeEntity(1L, "AUTO")
        );
    }
}
