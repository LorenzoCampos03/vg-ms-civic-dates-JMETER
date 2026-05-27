package pe.edu.vallegrande.sigei.civicDates.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    @SuppressWarnings("unchecked")
    void jwtAuthenticationConverter_shouldMapRealmAndResourceRoles() {
        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of(
                        "realm_access", Map.of("roles", List.of("admin", "secretaria")),
                        "resource_access", Map.of(
                                "client-a", Map.of("roles", List.of("manager")),
                                "client-b", Map.of("roles", List.of("auditor"))
                        )
                )
        );

        Mono<AbstractAuthenticationToken> tokenMono = (Mono<AbstractAuthenticationToken>) ReflectionTestUtils.invokeMethod(
                securityConfig,
                "jwtAuthenticationConverter",
                jwt
        );

        AbstractAuthenticationToken token = tokenMono.block();
        assertNotNull(token);

        Set<String> authorities = token.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());

        assertTrue(authorities.contains("ROLE_ADMIN"));
        assertTrue(authorities.contains("ROLE_SECRETARIA"));
        assertTrue(authorities.contains("ROLE_MANAGER"));
        assertTrue(authorities.contains("ROLE_AUDITOR"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void jwtAuthenticationConverter_shouldHandleMissingRoleClaims() {
        Jwt jwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", "user-1")
        );

        Mono<AbstractAuthenticationToken> tokenMono = (Mono<AbstractAuthenticationToken>) ReflectionTestUtils.invokeMethod(
                securityConfig,
                "jwtAuthenticationConverter",
                jwt
        );

        AbstractAuthenticationToken token = tokenMono.block();
        assertNotNull(token);
        assertNotNull(token.getAuthorities());
    }
}
