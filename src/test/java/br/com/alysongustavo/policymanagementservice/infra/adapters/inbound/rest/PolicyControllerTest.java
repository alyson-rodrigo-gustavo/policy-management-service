package br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest;

import br.com.alysongustavo.policymanagementservice.application.usecase.*;
import br.com.alysongustavo.policymanagementservice.application.usecase.input.CreatePolicyCommand;
import br.com.alysongustavo.policymanagementservice.application.usecase.output.RegisterPolicyResult;
import br.com.alysongustavo.policymanagementservice.domain.model.Policy;
import br.com.alysongustavo.policymanagementservice.domain.model.PolicyType;
import br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.PolicyController;
import br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.dto.request.RegisterPolicyRequest;
import br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.dto.response.PolicyResponse;
import br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.mapper.PolicyRestMapper;
import br.com.alysongustavo.policymanagementservice.infra.config.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PolicyController.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean private ListPolicyUseCase listPolicyUseCase;
    @MockitoBean private RegisterPolicyUseCase registerPolicyUseCase;
    @MockitoBean private FindByIdPolicyUseCase findByIdPolicyUseCase;
    @MockitoBean private DeletePolicyUseCase deletePolicyUseCase;
    @MockitoBean private EditPolicyUseCase editPolicyUseCase;
    @MockitoBean private PolicyRestMapper policyRestMapper;


    @Test
    @DisplayName("GET /policies/{id} - Should return 200 OK when user has MANAGER role")
    @WithMockUser(roles = "MANAGER")
    void shouldReturn200WhenManagerFetchesPolicyById() throws Exception {
        Long id = 1L;
        Policy domain = new Policy(id, "10102020", BigDecimal.TEN, BigDecimal.TEN, new PolicyType(1L, "AUTO"));

        PolicyResponse response = new PolicyResponse();
        response.setId(id);
        response.setDocument("10102020");
        response.setCoverageValue(BigDecimal.TEN);
        response.setPremiumValue(BigDecimal.TEN);
        response.setPolicyType(1L);

        when(findByIdPolicyUseCase.execute(id)).thenReturn(domain);
        when(policyRestMapper.toPolicyResponse(domain)).thenReturn(response);

        mockMvc.perform(get("/policies/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document").value("10102020"))
                .andExpect(jsonPath("$.coverageValue").value("10"))
                .andExpect(jsonPath("$.premiumValue").value("10"));
    }

    @Test
    @DisplayName("GET /policies/{id} - Should return 403 Forbidden when user does not have MANAGER role")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenUserWithoutManagerRoleFetchesPolicyById() throws Exception {
        mockMvc.perform(get("/policies/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /policies - Should return a list of policies")
    @WithMockUser(roles = "MANAGER")
    void shouldReturnPolicysList() throws Exception {
        Policy emp1 = new Policy(1L, "10102020", BigDecimal.TEN, BigDecimal.TEN, new PolicyType(1L, "AUTO"));
        Policy emp2 = new Policy(2L, "10103030", BigDecimal.TEN, BigDecimal.TEN, new PolicyType(2L, "AUTO"));

        PolicyResponse resp1 = new PolicyResponse();
        resp1.setId(1L);
        resp1.setDocument("10102020");
        resp1.setCoverageValue(BigDecimal.TEN);
        resp1.setPremiumValue(BigDecimal.TEN);
        resp1.setPolicyType(1L);

        PolicyResponse resp2 = new PolicyResponse();
        resp2.setId(2L);
        resp2.setDocument("20203030");
        resp2.setCoverageValue(BigDecimal.TEN);
        resp2.setPremiumValue(BigDecimal.TEN);
        resp2.setPolicyType(2L);

        when(listPolicyUseCase.execute()).thenReturn(List.of(emp1, emp2));
        when(policyRestMapper.toPolicyResponse(emp1)).thenReturn(resp1);
        when(policyRestMapper.toPolicyResponse(emp2)).thenReturn(resp2);

        mockMvc.perform(get("/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].document").value("10102020"));
    }

    @Test
    @DisplayName("POST /policies - Should create an policy when user has ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldCreatePolicyWhenAdmin() throws Exception {

        RegisterPolicyRequest request = new RegisterPolicyRequest(
                "10102020", BigDecimal.TEN, BigDecimal.TEN, 1L
        );

        var command = policyRestMapper.toCreatePolicyCommand(request);

        RegisterPolicyResult resultMock = new RegisterPolicyResult(
                1L,
                "10102020",
                BigDecimal.TEN,
                BigDecimal.TEN,
                1L
        );

        PolicyResponse response = new PolicyResponse();
        response.setId(1L);
        response.setDocument("10102020");
        response.setCoverageValue(BigDecimal.TEN);
        response.setPremiumValue(BigDecimal.TEN);
        response.setPolicyType(1L);


        when(policyRestMapper.toCreatePolicyCommand(any(RegisterPolicyRequest.class)))
                .thenReturn(mock(CreatePolicyCommand.class));

        when(registerPolicyUseCase.execute(any())).thenReturn(resultMock);

        when(policyRestMapper.toPolicyResponse(resultMock))
                .thenReturn(response);

        mockMvc.perform(post("/policies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.document").value("10102020"));
    }

    @Test
    @DisplayName("POST /policies - Should return 403 Forbidden when MANAGER tries to create an policy")
    @WithMockUser(roles = "MANAGER")
    void shouldReturn403WhenManagerTriesToCreatePolicy() throws Exception {
        RegisterPolicyRequest request = new RegisterPolicyRequest("10102020", BigDecimal.TEN,
                BigDecimal.TEN, 1L);

        mockMvc.perform(post("/policies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /policies - Should return 400 Bad Request when request body is invalid")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn400WhenRequestBodyIsInvalid() throws Exception {
        String emptyJson = "{}";

        mockMvc.perform(post("/policies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emptyJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /policies/{id} - Should delete policy and return 204 No Content when user has ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldDeletePolicyAndReturn204WhenAdmin() throws Exception {
        Long id = 1L;

        doNothing().when(deletePolicyUseCase).execute(id);

        mockMvc.perform(delete("/policies/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNoContent()); // 204

        verify(deletePolicyUseCase).execute(id);
    }
}
