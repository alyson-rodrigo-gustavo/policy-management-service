package br.com.alysongustavo.policymanagementservice.infra.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class KeycloakGrantedAuthoritiesConverterTest {

    private final KeycloakGrantedAuthoritiesConverter converter = new KeycloakGrantedAuthoritiesConverter();

    @Test
    @DisplayName("Should extract realm roles and convert them to ROLE_UPPERCASE")
    void shouldExtractRealmRolesAndConvertToUppercase() {
        Map<String, Object> realmAccess = Map.of(
                "roles", List.of("admin", "manager")
        );

        Jwt jwt = criarJwt(Map.of("realm_access", realmAccess));

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertThat(roles).contains("ROLE_ADMIN", "ROLE_MANAGER");
    }

    @Test
    @DisplayName("Should extract resource (client) roles from multiple clients")
    void shouldExtractResourceRolesFromMultipleClients() {
        Map<String, Object> resourceAccess = Map.of(
                "app-frontend", Map.of("roles", List.of("admin")),
                "app-backend", Map.of("roles", List.of("manager"))
        );

        Jwt jwt = criarJwt(Map.of("resource_access", resourceAccess));

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertThat(roles).contains("ROLE_ADMIN", "ROLE_MANAGER");
    }

    @Test
    @DisplayName("Should ignore missing or null claims without throwing exceptions")
    void shouldIgnoreMissingClaimsWithoutErrors() {
        Jwt jwt = criarJwt(Map.of());

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities).isEmpty();
    }

    @Test
    @DisplayName("Should merge realm roles, resource roles, and default OAuth2 scopes")
    void shouldMergeRolesAndScopes() {
        Map<String, Object> realmAccess = Map.of("roles", List.of("realm-admin"));
        Map<String, Object> resourceAccess = Map.of("my-client", Map.of("roles", List.of("client-user")));

        Map<String, Object> claims = Map.of(
                "realm_access", realmAccess,
                "resource_access", resourceAccess,
                "scope", "openid profile email"
        );

        Jwt jwt = criarJwt(claims);

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertThat(roles).contains(
                "ROLE_REALM-ADMIN", // Do Realm
                "ROLE_CLIENT-USER", // Do Resource
                "SCOPE_openid",     // Do Default Converter
                "SCOPE_email"       // Do Default Converter
        );
    }

    private Jwt criarJwt(Map<String, Object> claims) {
        return Jwt.withTokenValue("token-dummy")
                .header("alg", "none")
                .claims(c -> c.putAll(claims))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("test-user")
                .build();
    }
}
