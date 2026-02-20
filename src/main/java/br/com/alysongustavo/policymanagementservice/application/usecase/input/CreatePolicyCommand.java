package br.com.alysongustavo.policymanagementservice.application.usecase.input;

import java.math.BigDecimal;

public record CreatePolicyCommand(String document,
                                  BigDecimal premiumValue,
                                  BigDecimal coverageValue,
                                  Long policyType)
{}
