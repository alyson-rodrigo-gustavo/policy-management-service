package br.com.alysongustavo.policymanagementservice.infra.cache;

import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyTypeEntity;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.repository.PolicyTypeJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PolicyTypeCacheServiceTest {

    @Mock
    private PolicyTypeJpaRepository repository;

    @InjectMocks
    private PolicyTypeCacheService policyTypeCacheService;

    private List<PolicyTypeEntity> mockEntities;

    @BeforeEach
    void setUp() {
        mockEntities = List.of(
                new PolicyTypeEntity(1L, "AUTO"),
                new PolicyTypeEntity(2L, "LIFE"),
                new PolicyTypeEntity(3L, "HOME")
        );
    }

    @Test
    @DisplayName("Should return all PolicyTypeEntities directly from repository")
    void shouldReturnAllPolicyTypeEntities() {
        when(repository.findAll()).thenReturn(mockEntities);

        List<PolicyTypeEntity> result = policyTypeCacheService.getAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(mockEntities);

        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should map PolicyTypeEntities by name correctly")
    void shouldMapPolicyTypeEntitiesByName() {
        when(repository.findAll()).thenReturn(mockEntities);

        Map<String, PolicyTypeEntity> resultMap = policyTypeCacheService.getAllByName();

        assertThat(resultMap).isNotNull();
        assertThat(resultMap).hasSize(3);

        assertThat(resultMap.get("AUTO").getId()).isEqualTo(1L);
        assertThat(resultMap.get("LIFE").getId()).isEqualTo(2L);
        assertThat(resultMap.get("HOME").getId()).isEqualTo(3L);

        verify(repository).findAll();
    }
}