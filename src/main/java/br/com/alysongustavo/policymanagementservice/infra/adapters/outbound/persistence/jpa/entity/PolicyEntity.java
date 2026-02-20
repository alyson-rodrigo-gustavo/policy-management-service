package br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity;

import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "policy")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolicyEntity {

    @Id
    @SequenceGenerator(name = "policy_seq", sequenceName = "policy_seq", allocationSize = 1)
    @GeneratedValue(generator = "policy_seq", strategy = GenerationType.SEQUENCE)
    private Long id;
    private String document;
    private BigDecimal premiumValue;
    private BigDecimal coverageValue;

    @ManyToOne
    private PolicyTypeEntity policyType;

}
