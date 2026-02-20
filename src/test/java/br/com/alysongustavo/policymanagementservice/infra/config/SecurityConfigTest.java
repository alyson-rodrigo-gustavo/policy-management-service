package br.com.alysongustavo.policymanagementservice.infra.config;

import br.com.alysongustavo.policymanagementservice.infra.security.KeycloakGrantedAuthoritiesConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SecurityConfig.class)
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter;

    @Test
    @DisplayName("Should load SecurityFilterChain and supporting security beans")
    void shouldLoadSecurityBeans() {
        assertThat(context.containsBean("securityFilterChain")).isTrue();
        assertThat(context.containsBean("jwtAuthenticationConverter")).isTrue();
        assertThat(context.containsBean("jwtGrantedAuthoritiesConverter")).isTrue();
    }

    @Test
    @DisplayName("Should reject anonymous requests with 401 Unauthorized")
    void shouldRejectAnonymousRequests() throws Exception {
        mockMvc.perform(get("/api/anything"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should allow requests with a valid JWT")
    void shouldAllowRequestsWithValidJwt() throws Exception {
        mockMvc.perform(get("/api/anything")
                        .with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should not require CSRF token for POST requests when using JWT authentication")
    void shouldNotRequireCsrfTokenForPostRequests() throws Exception {
        mockMvc.perform(post("/api/test")
                        .with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should configure principal claim name as preferred_username")
    void shouldConfigurePrincipalClaimName() {
        assertThat(jwtAuthenticationConverter).isInstanceOf(JwtAuthenticationConverter.class);

        JwtAuthenticationConverter converter = (JwtAuthenticationConverter) jwtAuthenticationConverter;

        String claimName = (String) ReflectionTestUtils.getField(converter, "principalClaimName");

        assertThat(claimName).isEqualTo("preferred_username");
    }

    @Test
    @DisplayName("Should configure KeycloakGrantedAuthoritiesConverter as the authorities converter")
    void shouldConfigureCustomAuthoritiesConverter() {
        JwtAuthenticationConverter converter = (JwtAuthenticationConverter) jwtAuthenticationConverter;

        Object authoritiesConverter = ReflectionTestUtils.getField(converter, "jwtGrantedAuthoritiesConverter");

        assertThat(authoritiesConverter).isInstanceOf(KeycloakGrantedAuthoritiesConverter.class);
    }

}
