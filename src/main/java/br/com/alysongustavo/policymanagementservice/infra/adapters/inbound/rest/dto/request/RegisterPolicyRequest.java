package br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPolicyRequest {

    @NotEmpty
    @Size(max = 50)
    private String document;

    @NotNull
    private BigDecimal premiumValue;

    @NotNull
    private BigDecimal coverageValue;

    @NotNull
    private Long policyType;

}
