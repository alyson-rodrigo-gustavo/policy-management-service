package br.com.alysongustavo.policymanagementservice.infra.config;

import br.com.alysongustavo.policymanagementservice.domain.service.PolicyCoverageEligibilityRule;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class DomainBeansConfig {

    @Bean
    public PolicyCoverageEligibilityRule validatePolicyCoverageEligibilityRule() {
        return new PolicyCoverageEligibilityRule();
    }

}
