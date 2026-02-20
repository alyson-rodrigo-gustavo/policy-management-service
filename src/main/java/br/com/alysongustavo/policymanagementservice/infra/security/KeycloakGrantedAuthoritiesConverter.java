package br.com.alysongustavo.policymanagementservice.infra.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();
        Collection<GrantedAuthority> authorities = defaultConverter.convert(jwt);

        Collection<GrantedAuthority> realmRoles = extractRealmRoles(jwt);
        Collection<GrantedAuthority> resourceRoles = extractResourceRoles(jwt);

        return Stream.of(authorities, realmRoles, resourceRoles)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) {
            return List.of();
        }

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get("roles");
        if (roles == null) {
            return List.of();
        }

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }

    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess == null) {
            return List.of();
        }

        return resourceAccess.entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Map)
                .flatMap(entry -> {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> resource = (Map<String, Object>) entry.getValue();
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) resource.get("roles");

                    if (roles == null) {
                        return Stream.empty();
                    }

                    return roles.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                })
                .collect(Collectors.toList());
    }
}
