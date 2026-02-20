package br.com.alysongustavo.policymanagementservice.application.usecase;

import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyByIdNotFoundException;
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
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FindByIdPolicyUseCaseTest {

    private static final Long POLICY_ID = 1L;

    @Mock
    private PolicyRepositoryPort policyRepositoryPort;

    @InjectMocks
    private FindByIdPolicyUseCase findByIdPolicyUseCase;

    @Test
    @DisplayName("Should throw PolicyByIdNotFoundException when policy is not found")
    public void shouldThrowPolicyByIdNotFoundExceptionWhenPolicyIsNotFound() {

        when(policyRepositoryPort.findById(POLICY_ID)).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(
                PolicyByIdNotFoundException.class,
                () -> findByIdPolicyUseCase.execute(POLICY_ID)
        );

        verify(policyRepositoryPort, times(1)).findById(POLICY_ID);
    }

    @Test
    @DisplayName("Should return policy when policy exists")
    public void shouldReturnPolicyWhenPolicyExists() {
        Long id = 1L;
        var policy = new Policy(
                id, "10102020", BigDecimal.valueOf(1000), BigDecimal.valueOf(1000),
                new PolicyType(1L, "AUTO")
        );

        when(policyRepositoryPort.findById(id)).thenReturn(Optional.of(policy));

        var result = findByIdPolicyUseCase.execute(id);
        assertNotNull(result);
        assertEquals(policy, result);
        verify(policyRepositoryPort, times(1)).findById(id);

    }
}

