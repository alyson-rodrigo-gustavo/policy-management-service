package br.com.alysongustavo.policymanagementservice.application.usecase;

import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import br.com.alysongustavo.policymanagementservice.domain.port.outbound.PolicyRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListPolicyUseCaseTest {

    @Mock
    private PolicyRepositoryPort policyRepositoryPort;

    @InjectMocks
    private ListPolicyUseCase listPolicyUseCase;

    @Test
    @DisplayName("Should return all policies")
    public void shouldReturnAllPolicies() {
        List<Policy> employeeList = List.of(
                new Policy(1L, "10102020", BigDecimal.valueOf(1000), BigDecimal.valueOf(10000), new PolicyType(1L, "AUTO")),
                new Policy(2L, "10103030", BigDecimal.valueOf(2000), BigDecimal.valueOf(20000), new PolicyType(2L, "HOME")),
                new Policy(3L, "10104040", BigDecimal.valueOf(3000), BigDecimal.valueOf(30000), new PolicyType(3L, "LIFE"))
        );

        when(policyRepositoryPort.findAll()).thenReturn(employeeList);

        List<Policy> policies = listPolicyUseCase.execute();

        assertEquals(employeeList, policies);
        assertEquals(3, policies.size());
        verify(policyRepositoryPort).findAll();
    }

    @Test
    @DisplayName("Should return an empty list when no policies exist")
    public void shouldReturnEmptyListWhenNoPoliciesExist() {
        when(policyRepositoryPort.findAll()).thenReturn(List.of());

        List<Policy> policies = listPolicyUseCase.execute();

        assertNotNull(policies);
        assertTrue(policies.isEmpty());
        verify(policyRepositoryPort).findAll();
    }
}
