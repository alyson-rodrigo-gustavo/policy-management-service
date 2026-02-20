package br.com.alysongustavo.policymanagementservice.application.usecase;

import br.com.alysongustavo.policymanagementservice.application.mapper.PolicyMapper;
import br.com.alysongustavo.policymanagementservice.application.usecase.input.CreatePolicyCommand;
import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyCoverageException;
import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyExistException;
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
public class RegisterPolicyUseCaseTest {

    @Mock
    private PolicyCoverageEligibilityRule validatePolicyCoverageEligibilityRule;
    @Mock
    private PolicyRepositoryPort policyRepositoryPort;
    @Mock
    private PolicyTypeRepositoryPort policyTypeRepositoryPort;

    @Mock
    private PolicyMapper policyMapper;

    @InjectMocks
    private RegisterPolicyUseCase registerPolicyUseCase;

    private CreatePolicyCommand command;

    @BeforeEach
    public void setup(){

        this.command = new CreatePolicyCommand(
                "10102020",
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1000),
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
                () -> registerPolicyUseCase.execute(command));
        verifyNoInteractions(policyRepositoryPort, policyTypeRepositoryPort);
    }

    @Test
    @DisplayName("Should throw PolicyExistException when document already exists")
    public void shouldThrowPolicyExistExceptionWhenPolicyAlreadyExists() {
        when(policyRepositoryPort.existsByDocument(command.document())).thenReturn(true);

        assertThrows(PolicyExistException.class, () -> registerPolicyUseCase.execute(command));

        verify(validatePolicyCoverageEligibilityRule).validateCoverage(any(), any());

        verify(policyRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw PolicyTypeNotFoundException when policy type does not exist in cache")
    public void shouldThrowPolicyTypeNotFoundExceptionWhenPolicyTypeDoesNotExist() {
        when(policyRepositoryPort.existsByDocument(command.document())).thenReturn(false);
        when(policyTypeRepositoryPort.findById(command.policyType())).thenReturn(Optional.empty());

        assertThrows(PolicyTypeNotFoundException.class, () -> registerPolicyUseCase.execute(command));

        verify(policyTypeRepositoryPort).findById(command.policyType());
        verify(policyRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should register policy successfully when all validations pass")
    public void shouldRegisterPolicySuccessfullyWhenAllValidationsPass() {
        PolicyType validPolicyType = new PolicyType(1L, "AUTO");
        Policy mappedPolicy = new Policy(
                null,
                command.document(),
                command.premiumValue(),
                command.coverageValue(),
                null
        );

        when(policyRepositoryPort.existsByDocument(command.document())).thenReturn(false);
        when(policyTypeRepositoryPort.findById(command.policyType())).thenReturn(Optional.of(validPolicyType));
        when(policyMapper.toDomain(command)).thenReturn(mappedPolicy);
        when(policyRepositoryPort.save(any(Policy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        registerPolicyUseCase.execute(command);

        verify(validatePolicyCoverageEligibilityRule).validateCoverage(any(), any());
        verify(policyTypeRepositoryPort).findById(command.policyType());
        verify(policyRepositoryPort).save(mappedPolicy);
        verify(policyMapper).toRegisterPolicyResult(mappedPolicy);
    }
}
