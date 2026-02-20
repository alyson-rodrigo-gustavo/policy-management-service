package br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "policy_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PolicyTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "policy_type_seq")
    @SequenceGenerator(name = "policy_type_seq", sequenceName = "policy_type_seq", allocationSize = 1)
    private Long id;

    private String name;

}
