package br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PolicyResponse {

    private Long id;
    private String document;
    private BigDecimal premiumValue;
    private BigDecimal coverageValue;
    private Long policyType;
}
