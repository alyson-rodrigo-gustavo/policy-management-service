package br.com.alysongustavo.policymanagementservice.application.usecase;

import br.com.alysongustavo.policymanagementservice.application.mapper.PolicyMapper;
import br.com.alysongustavo.policymanagementservice.application.usecase.input.CreatePolicyCommand;
import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyCoverageException;
import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyNotFoundException;
import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyTypeNotFoundException;
import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import br.com.alysongustavo.policymanagementservice.domain.port.outbound.PolicyRepositoryPort;
import br.com.alysongustavo.policymanagementservice.domain.port.outbound.PolicyTypeRepositoryPort;
import br.com.alysongustavo.policymanagementservice.domain.service.PolicyCoverageEligibilityRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EditPolicyUseCaseTest {

    @Mock
    private PolicyCoverageEligibilityRule validatePolicyCoverageEligibilityRule;
    @Mock
    private PolicyRepositoryPort policyRepositoryPort;
    @Mock
    private PolicyTypeRepositoryPort policyTypeRepositoryPort;
    @Mock
    private PolicyMapper policyMapper;

    @InjectMocks
    private EditPolicyUseCase editPolicyUseCase;

    private CreatePolicyCommand command;

    private final Long id = 1L;

    @BeforeEach
    public void setup(){

        this.command = new CreatePolicyCommand(
                "10102020",
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(30000),
                1L
        );
    }

    @Test
    @DisplayName("Should throw PolicyCoverageException when premium and coverage values are invalid")
    public void shouldThrowPolicyCoverageExceptionWhenCoverageIsInvalid(){
        doThrow(new PolicyCoverageException(command.coverageValue(), command.premiumValue()))
                .when(validatePolicyCoverageEligibilityRule)
                .validateCoverage(any(), any());

        assertThrows(PolicyCoverageException.class,
                () -> editPolicyUseCase.execute(command, id));

        verifyNoInteractions(policyRepositoryPort, policyTypeRepositoryPort);
    }

    @Test
    @DisplayName("Should throw PolicyNotFoundException when policy does not exist")
    public void shouldThrowPolicyNotFoundExceptionWhenPolicyDoesNotExist(){
        doNothing().when(validatePolicyCoverageEligibilityRule).validateCoverage(any(), any());

        // Simulando que a apólice não foi encontrada
        when(policyRepositoryPort.existsByDocument(command.document())).thenReturn(false);

        assertThrows(PolicyNotFoundException.class,
                () -> editPolicyUseCase.execute(command, id));

        verify(policyRepositoryPort).existsByDocument(command.document());
        verifyNoMoreInteractions(policyRepositoryPort);
        verifyNoInteractions(policyTypeRepositoryPort);
    }

    @Test
    @DisplayName("Should throw PolicyTypeNotFoundException when policy type does not exist in cache")
    public void shouldThrowPolicyTypeNotFoundExceptionWhenPolicyTypeDoesNotExist() {
        doNothing().when(validatePolicyCoverageEligibilityRule).validateCoverage(any(), any());

        when(policyRepositoryPort.existsByDocument(command.document())).thenReturn(true);
        when(policyTypeRepositoryPort.findById(command.policyType())).thenReturn(Optional.empty());

        assertThrows(PolicyTypeNotFoundException.class, () -> editPolicyUseCase.execute(command, id));

        verify(policyTypeRepositoryPort).findById(command.policyType());
        verify(policyRepositoryPort, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should update policy when all validations pass")
    public void shouldUpdatePolicyWhenAllValidationsPass() {
        doNothing().when(validatePolicyCoverageEligibilityRule).validateCoverage(any(), any());

        PolicyType validPolicyType = new PolicyType(1L, "Life Insurance");
        Policy mappedPolicy = new Policy(
                id,
                command.document(),
                command.premiumValue(),
                command.coverageValue(),
                null
        );

        when(policyRepositoryPort.existsByDocument(command.document())).thenReturn(true);
        when(policyTypeRepositoryPort.findById(command.policyType())).thenReturn(Optional.of(validPolicyType));
        when(policyMapper.toDomain(any())).thenReturn(mappedPolicy);
        when(policyRepositoryPort.update(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        editPolicyUseCase.execute(command, id);

        verify(policyRepositoryPort).existsByDocument(command.document());
        verify(policyTypeRepositoryPort).findById(command.policyType());
        verify(policyRepositoryPort).update(any(), eq(id));
    }
}
