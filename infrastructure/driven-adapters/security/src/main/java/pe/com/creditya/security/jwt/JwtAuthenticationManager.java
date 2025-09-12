package pe.com.creditya.security.jwt;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pe.com.creditya.model.common.exceptions.InvalidCredentialsException;
import pe.com.creditya.security.common.constants.Constants;
import reactor.core.publisher.Mono;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .filter(auth -> auth.getCredentials() != null)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException(Constants.NOT_SENDED_TOKEN)))
                .flatMap(auth -> jwtProvider.validateTokenAndGetClaims(auth.getCredentials().toString())
                        .map(this::createAuthentication)
                )
                .onErrorResume(e -> {
                    log.warn(Constants.LOGGER_AUTHENTICATION_FAILED, e.getMessage());
                    return Mono.error(new InvalidCredentialsException(Constants.INVALID_JWT_TOKEN, e));
                });
    }


    private Authentication createAuthentication(Claims claims) {
        String username = claims.getSubject();
        Collection<GrantedAuthority> authorities = extractAuthorities(claims);

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractAuthorities(Claims claims) {
        try {
            List<String> roles = claims.get(Constants.PREFIX_ROLES, List.class);

            if (roles == null || roles.isEmpty()) {
                log.warn("No roles found in JWT token for user: {}", claims.getSubject());
                return List.of(new SimpleGrantedAuthority(Constants.ROLE_USER));
            }

            return roles.stream()
                    .map(role -> role.startsWith(Constants.PREFIX_TOKEN) ? role : Constants.PREFIX_TOKEN + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.warn("Error extracting authorities from JWT token: {}", e.getMessage());
            return List.of(new SimpleGrantedAuthority(Constants.ROLE_USER));
        }
    }
}
