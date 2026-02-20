package br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence;

import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyEntity;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyTypeEntity;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.mapper.PolicyJpaMapper;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.repository.PolicyJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyRepositoryAdapterTest {

    private static final Long POLICY_ID = 1L;

    @Mock
    private PolicyJpaRepository policyJpaRepository;

    @Mock
    private PolicyJpaMapper policyJpaMapper;

    @InjectMocks
    private PolicyRepositoryAdapter policyRepositoryAdapter;

    @Test
    @DisplayName("Should save policy and return domain model with generated ID")
    void shouldSavePolicyAndReturnDomainWithId() {
        Policy inputDomain = new Policy(
                null, "10102020", BigDecimal.TEN, BigDecimal.TEN,
                new PolicyType(1L, "AUTO")
        );

        PolicyEntity entityToSave = new PolicyEntity(
                null, "10102020", BigDecimal.TEN, BigDecimal.TEN,
                new PolicyTypeEntity(1L, "AUTO")
        );

        PolicyEntity savedEntity = new PolicyEntity(
                POLICY_ID, "10102020", BigDecimal.TEN, BigDecimal.TEN,
                new PolicyTypeEntity(1L, "AUTO")
        );

        Policy expectedDomain = new Policy(
                POLICY_ID, "10102020", BigDecimal.TEN, BigDecimal.TEN,
                new PolicyType(1L, "AUTO")
        );

        when(policyJpaMapper.toEntity(inputDomain)).thenReturn(entityToSave);
        when(policyJpaRepository.save(entityToSave)).thenReturn(savedEntity);
        when(policyJpaMapper.toDomain(savedEntity)).thenReturn(expectedDomain);

        Policy result = policyRepositoryAdapter.save(inputDomain);

        assertNotNull(result);
        assertEquals(POLICY_ID, result.getId());
        assertEquals("10102020", result.getDocument());

        verify(policyJpaMapper).toEntity(inputDomain);
        verify(policyJpaRepository).save(entityToSave);
        verify(policyJpaMapper).toDomain(savedEntity);
        verifyNoMoreInteractions(policyJpaRepository, policyJpaMapper);
    }

    @Test
    @DisplayName("Should return true when policy exists by document")
    void shouldReturnTrueWhenPolicyExistsByDocument() {
        when(policyJpaRepository.findByDocument("10102020"))
                .thenReturn(java.util.Optional.of(new PolicyEntity()));

        boolean exists = policyRepositoryAdapter.existsByDocument("10102020");

        assertTrue(exists);
        verify(policyJpaRepository).findByDocument("10102020");
    }

    @Test
    @DisplayName("Should return false when policy does not exist by document")
    void shouldReturnFalseWhenPolicyDoesNotExistByDocument() {
        when(policyJpaRepository.findByDocument("00000000000"))
                .thenReturn(java.util.Optional.empty());

        boolean exists = policyRepositoryAdapter.existsByDocument("00000000000");

        assertFalse(exists);
        verify(policyJpaRepository).findByDocument("00000000000");
    }
}