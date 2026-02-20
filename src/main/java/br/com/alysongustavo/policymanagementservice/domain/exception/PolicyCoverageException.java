package br.com.alysongustavo.policymanagementservice.domain.exception;

import java.math.BigDecimal;

public class PolicyCoverageException extends BusinessException {

    public PolicyCoverageException(BigDecimal coverageValue,
                                   BigDecimal premiumValue) {
        super(
                "POLICY_COVERAGE_NOT_ALLOWED",
                coverageValue,
                premiumValue
        );
    }
}