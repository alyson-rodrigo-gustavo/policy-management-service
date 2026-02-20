package br.com.alysongustavo.policymanagementservice.infra.config;

import br.com.alysongustavo.policymanagementservice.domain.service.PolicyCoverageEligibilityRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = DomainBeansConfig.class)
public class DomainBeansConfigTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private PolicyCoverageEligibilityRule validatePolicyCoverageEligibilityRule;

    @Test
    @DisplayName("Should load application context and register all domain beans")
    void shouldLoadContextAndRegisterAllDomainBeans() {
        assertThat(validatePolicyCoverageEligibilityRule).isNotNull();
        assertThat(context.containsBean("validatePolicyCoverageEligibilityRule")).isTrue();
    }

}
