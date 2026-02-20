package br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.mapper;

import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyEntity;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyTypeEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PolicyJpaMapperTest.TestConfig.class)
public class PolicyJpaMapperTest {

    @Configuration
    @ComponentScan(basePackageClasses = PolicyJpaMapper.class)
    static class TestConfig {}

    @Autowired
    private PolicyJpaMapper policyJpaMapper;

    @Test
    @DisplayName("Should map Policy domain model to PolicyEntity correctly")
    void shouldMapDomainToEntity() {
        Policy policy =
                new Policy(1L, "10102020" ,BigDecimal.TEN, BigDecimal.TEN, new PolicyType(1L, "AUTO"));


        PolicyEntity policyEntity = policyJpaMapper.toEntity(policy);

        assertThat(policyEntity).isNotNull();
        assertThat(policyEntity.getId()).isEqualTo(policy.getId());
        assertThat(policyEntity.getDocument()).isEqualTo(policy.getDocument());
        assertThat(policyEntity.getCoverageValue()).isEqualTo(policy.getCoverageValue());
        assertThat(policyEntity.getPremiumValue()).isEqualTo(policy.getPremiumValue());
        assertThat(policyEntity.getPolicyType().getId()).isEqualTo(policy.getPolicyType().getId());

    }

    @Test
    @DisplayName("Should map PolicyEntity to Policy domain model correctly")
    void shouldMapEntityToDomain() {
        PolicyEntity policyEntity =
                new PolicyEntity(1L, "10102020", BigDecimal.TEN, BigDecimal.TEN, new PolicyTypeEntity(1L, "AUTO"));


        Policy policy = policyJpaMapper.toDomain(policyEntity);

        assertThat(policy).isNotNull();
        assertThat(policy.getId()).isEqualTo(policyEntity.getId());
        assertThat(policy.getDocument()).isEqualTo(policyEntity.getDocument());
        assertThat(policy.getCoverageValue()).isEqualTo(policyEntity.getCoverageValue());
        assertThat(policy.getPremiumValue()).isEqualTo(policyEntity.getPremiumValue());
        assertThat(policy.getPolicyType().getId()).isEqualTo(policyEntity.getPolicyType().getId());

    }


}
