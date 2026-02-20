package br.com.alysongustavo.policymanagementservice.application.usecase.output;

import java.math.BigDecimal;

public record RegisterPolicyResult(
        Long id,
        String document,
        BigDecimal premiumValue,
        BigDecimal coverageValue,
        Long policyType
) {}
