package br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.mapper;

import br.com.alysongustavo.policymanagementservice.application.usecase.output.RegisterPolicyResult;
import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.dto.request.RegisterPolicyRequest;
import br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.dto.response.PolicyResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PolicyRestMapperTest.TestConfig.class)
public class PolicyRestMapperTest {

    @Configuration
    @ComponentScan(basePackageClasses = PolicyRestMapper.class)
    static class TestConfig {}

    @Autowired
    private PolicyRestMapper policyRestMapper;

    @Test
    @DisplayName("Should map Policy domain model to PolicyResponse DTO")
    void shouldMapDomainParaResponse() {
        Policy policy =
                new Policy(1L, "10102020",
                        BigDecimal.TEN, BigDecimal.TEN, new PolicyType(1L, "AUTO"));


        PolicyResponse response = policyRestMapper.toPolicyResponse(policy);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(policy.getId());
        assertThat(response.getDocument()).isEqualTo(policy.getDocument());
        assertThat(response.getCoverageValue()).isEqualTo(policy.getCoverageValue());
        assertThat(response.getPremiumValue()).isEqualTo(policy.getPremiumValue());
        assertThat(response.getPolicyType()).isEqualTo(policy.getPolicyType().getId());
    }

    @Test
    @DisplayName("Should map RegisterPolicyRequest to CreatePolicyCommand")
    void shouldMapRequestToCommand() {
        RegisterPolicyRequest request = new RegisterPolicyRequest(
                "10102020", BigDecimal.TEN, BigDecimal.TEN, 1L
        );

        var command = policyRestMapper.toCreatePolicyCommand(request);

        assertThat(command).isNotNull();
        assertThat(command.document()).isEqualTo(request.getDocument());
        assertThat(command.coverageValue()).isEqualTo(request.getCoverageValue());
        assertThat(command.premiumValue()).isEqualTo(request.getPremiumValue());
        assertThat(command.policyType()).isEqualTo(request.getPolicyType());
    }

    @Test
    @DisplayName("Should map RegisterPolicyResult to PolicyResponse DTO")
    void shouldMapResultToResponse() {

        RegisterPolicyResult result = new RegisterPolicyResult(
                1L, "10102020", BigDecimal.TEN, BigDecimal.TEN, 1L
        );

        var response = policyRestMapper.toPolicyResponse(result);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(result.id());
        assertThat(response.getDocument()).isEqualTo(result.document());
        assertThat(response.getCoverageValue()).isEqualTo(result.coverageValue());
        assertThat(response.getPremiumValue()).isEqualTo(result.premiumValue());
        assertThat(response.getPolicyType()).isEqualTo(result.policyType());
    }

}
