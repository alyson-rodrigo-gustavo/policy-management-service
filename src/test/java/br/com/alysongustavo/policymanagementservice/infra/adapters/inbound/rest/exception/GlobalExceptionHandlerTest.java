package br.com.alysongustavo.policymanagementservice.infra.adapters.inbound.rest.exception;

import br.com.alysongustavo.policymanagementservice.domain.exception.BusinessException;
import br.com.alysongustavo.policymanagementservice.domain.exception.PolicyNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestController.class)
@Import({GlobalExceptionHandler.class, GlobalExceptionHandlerTest.Config.class})
@WithMockUser
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class Config {
        @Bean
        public TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {
        @GetMapping("/test/business")
        public void business() {
            throw new BusinessException("rule.invalid", "Business rule violated");
        }

        @GetMapping("/test/not-found")
        public void notFound() {
            throw new PolicyNotFoundException("04531160002");
        }

        @GetMapping("/test/generic")
        public void generic() {
            throw new RuntimeException("Unexpected error");
        }
    }

    @Test
    @DisplayName("Should return 422 Unprocessable Entity for BusinessException")
    void shouldReturn422WhenBusinessExceptionIsThrown() throws Exception {
        mockMvc.perform(get("/test/business")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.code").value("rule.invalid"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should return 404 Not Found for PolicyNotFoundException")
    void shouldReturn404WhenPolicyNotFoundExceptionIsThrown() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("POLICY_NOT_FOUND_BY_DOCUMENT"));
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error for unexpected exceptions")
    void shouldReturn500WhenGenericExceptionIsThrown() throws Exception {
        mockMvc.perform(get("/test/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("UNEXPECTED_ERROR"))
                .andExpect(jsonPath("$.message").value("Erro inesperado")); // Mensagem amigável, não o stacktrace
    }
}
