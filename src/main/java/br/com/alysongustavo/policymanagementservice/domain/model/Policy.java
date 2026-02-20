package br.com.alysongustavo.policymanagementservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Policy {

    private Long id;
    private String document;
    private BigDecimal premiumValue;
    private BigDecimal coverageValue;
    private PolicyType policyType;
}
