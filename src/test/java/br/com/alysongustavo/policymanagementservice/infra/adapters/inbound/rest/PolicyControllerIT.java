package br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest;

import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyCoverageException;
import br.com.alysongustavo.policymanagementservice.domain.service.PolicyCoverageEligibilityRule;
import br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.dto.request.RegisterPolicyRequest;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyEntity;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.entity.PolicyTypeEntity;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.repository.PolicyJpaRepository;
import br.com.alysongustavo.policymanagementservice.infra.adapters.outbound.persistence.jpa.repository.PolicyTypeJpaRepository;
import br.com.alysongustavo.policymanagementservice.infra.config.TestSecurityConfig;
import org.assertj.core.condition.AnyOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import br.com.alysongustavo.policymanagementservice.TestPolicyManagementServiceApplication;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Import({TestSecurityConfig.class, TestPolicyManagementServiceApplication.class})
public class PolicyControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PolicyCoverageEligibilityRule validatePolicyCoverageEligibilityRule;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PolicyJpaRepository policyJpaRepository;

    @Autowired
    private PolicyTypeJpaRepository policyTypeJpaRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setup() {
        policyJpaRepository.deleteAll();

        if (!policyTypeJpaRepository.existsById(1L)) {
            PolicyTypeEntity tipoAuto = new PolicyTypeEntity();
            tipoAuto.setName("AUTO");
            policyTypeJpaRepository.save(tipoAuto);
        }

        var cache = cacheManager.getCache("policyTypes");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    @DisplayName("Integration: Should create an policy and persist it to the database (end-to-end flow)")
    @WithMockUser(roles = "ADMIN")
    void shouldCreatePolicyAndPersistToDatabaseEndToEnd() throws Exception {

        RegisterPolicyRequest request = new RegisterPolicyRequest(
                "10102020", BigDecimal.valueOf(1000), BigDecimal.valueOf(10000), 1L
        );

        mockMvc.perform(post("/policies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists()) // Garante que retornou um ID
                .andExpect(jsonPath("$.document").value("10102020"));

        Optional<PolicyEntity> salvoNoBanco = policyJpaRepository.findByDocument("10102020");

        assertThat(salvoNoBanco).isPresent();
        assertThat(salvoNoBanco.get().getDocument()).isEqualTo("10102020");
        assertThat(salvoNoBanco.get().getPremiumValue()).isEqualByComparingTo("1000");
        assertThat(salvoNoBanco.get().getCoverageValue()).isEqualByComparingTo("10000");
    }

    @Test
    @DisplayName("Integration: Should reject policy creation when CPF validation fails (external service)")
    @WithMockUser(roles = "ADMIN")
    void shouldRejectPolicyCreationWhenRulePolicyCoverageEligibilityValidationFails() throws Exception {
        RegisterPolicyRequest request = new RegisterPolicyRequest(
                "10102020", BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), 1L);

        mockMvc.perform(post("/policies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent());

        assertThat(policyJpaRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("Integration: Should retrieve an policy from the database")
    @WithMockUser(roles = "MANAGER")
    void shouldRetrievePolicyFromDatabase() throws Exception {
        PolicyEntity entity = new PolicyEntity(null, "10102020", BigDecimal.valueOf(1000), BigDecimal.valueOf(1000),
                new PolicyTypeEntity(1L, "AUTO"));

        PolicyEntity salvo = policyJpaRepository.save(entity);

        mockMvc.perform(get("/policies/{id}", salvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document").value("10102020"));
    }



}
