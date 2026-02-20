package br.com.alysongustavo.policymanagementservice.application.mapper;

import br.com.alysongustavo.policymanagementservice.application.usecase.input.CreatePolicyCommand;
import br.com.alysongustavo.policymanagementservice.application.usecase.output.RegisterPolicyResult;
import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PolicyMapperTest.TestConfig.class)
public class PolicyMapperTest {

    @Configuration
    @ComponentScan(basePackageClasses = PolicyMapper.class)
    static class TestConfig {}

    @Autowired
    private PolicyMapper policyMapper;

    @Test
    @DisplayName("Should map CreatePolicyCommand to Policy domain model correctly")
    void shouldMapCommandToDomain() {

        CreatePolicyCommand command = new CreatePolicyCommand(
                "10102020",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(10000),
                1L
        );

        Policy domain = policyMapper.toDomain(command);

        assertThat(domain).isNotNull();
        assertThat(domain.getDocument()).isEqualTo(command.document());
        assertThat(domain.getCoverageValue()).isEqualTo(command.coverageValue());
        assertThat(domain.getPremiumValue()).isEqualTo(command.premiumValue());
        assertThat(domain.getPolicyType().getId()).isEqualTo(command.policyType());

    }

    @Test
    @DisplayName("Should map Policy domain model to RegisterPolicyResult correctly")
    void shouldMapDomainToResult() {
        Policy policy =
                new Policy(1L, "10102020", BigDecimal.valueOf(1000), BigDecimal.valueOf(30000), new PolicyType(1L, "AUTO"));

        RegisterPolicyResult result = policyMapper.toRegisterPolicyResult(policy);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(policy.getId());
        assertThat(result.document()).isEqualTo(policy.getDocument());
        assertThat(result.premiumValue()).isEqualTo(policy.getPremiumValue());
        assertThat(result.coverageValue()).isEqualTo(policy.getCoverageValue());
        assertThat(result.policyType()).isEqualTo(policy.getPolicyType().getId());


    }
}
