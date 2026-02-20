package br.com.alysongustavo.policymanagementservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyType {

    private Long id;
    private String name;
}
