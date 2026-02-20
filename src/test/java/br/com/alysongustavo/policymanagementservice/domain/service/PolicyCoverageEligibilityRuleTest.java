package br.com.alysongustavo.appjavapracticeobservability.domain.service;

import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyCoverageException;
import br.com.alysongustavo.policymanagementservice.domain.service.PolicyCoverageEligibilityRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PolicyCoverageEligibilityRuleTest {

    @InjectMocks
    private PolicyCoverageEligibilityRule validatePolicyCoverageEligibilityRule;

    @ParameterizedTest(name = "Should throw exception when premium {0} is NOT 10x coverage {1}")
    @CsvSource({
            "100, 20",   // 100 é 5x 20 (Falha)
            "50, 50",    // 50 é 1x 50 (Falha)
            "1000, 200" // 1000 é 5x 200 (Falha)
    })
    @DisplayName("Should throw PolicyCoverageException when premium is not exactly 10x greater than coverage")
    public void shouldThrowExceptionWhenPremiumIsNotTenTimesCoverage(BigDecimal premiumValue, BigDecimal coverageValue) {

        assertThrows(PolicyCoverageException.class, () -> validatePolicyCoverageEligibilityRule.validateCoverage(premiumValue, coverageValue));
    }

    @ParameterizedTest(name = "Should pass when premium {0} is exactly 10x coverage {1}")
    @CsvSource({
            "100, 1000",   // Exatamente 10x (Passa)
            "100, 1500",   // Maior que 10x (Passa)
            "50, 500",     // Exatamente 10x (Passa)
            "150, 5000"    // Muito maior que 10x (Passa)
    })
    @DisplayName("Should not throw exception when premium is 10x greater than coverage")
    public void shouldNotThrowExceptionWhenPremiumIsTenTimesCoverage(BigDecimal premiumValue, BigDecimal coverageValue) {

        assertDoesNotThrow(
                () -> validatePolicyCoverageEligibilityRule.validateCoverage(premiumValue, coverageValue));
    }
}
