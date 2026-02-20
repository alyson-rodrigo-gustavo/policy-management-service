package br.com.alysongustavo.policymanagementservice.domain.service;

import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyCoverageException;

import java.math.BigDecimal;

public class PolicyCoverageEligibilityRule {

    public void validateCoverage(BigDecimal premiumValue,
                                 BigDecimal coverageValue) {

        BigDecimal minimumCoverage =
                premiumValue.multiply(BigDecimal.TEN);

        if ( coverageValue.compareTo(minimumCoverage)
                < 0) {
            throw new PolicyCoverageException(
                    coverageValue,
                    premiumValue
            );
        }
    }
}
