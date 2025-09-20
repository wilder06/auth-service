package pe.com.creditya.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import pe.com.creditya.model.common.exceptions.InvalidCredentialsException;
import pe.com.creditya.model.common.exceptions.TokenValidationException;
import pe.com.creditya.security.common.constants.AuthConstants;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtProvider jwtProvider;
    private final AuthoritiesExtractor authoritiesExtractor;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.justOrEmpty(authentication)
                .filter(this::hasValidCredentials)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException(AuthConstants.NOT_SENDED_TOKEN)))
                .flatMap(this::processAuthentication)
                .onErrorResume(this::handleAuthenticationError);
    }

    private boolean hasValidCredentials(Authentication auth) {
        return auth.getCredentials() != null;
    }

    private Mono<Authentication> processAuthentication(Authentication auth) {
        String token = extractToken(auth);

        return jwtProvider.validateTokenAndGetClaims(token)
                .map(this::createAuthentication)
                .doOnSuccess(this::logSuccessfulAuthentication)
                .doOnError(error -> logFailedAuthentication(token, error));
    }

    private String extractToken(Authentication auth) {
        return auth.getCredentials().toString();
    }

    private Authentication createAuthentication(Claims claims) {
        String username = getUsernameFromClaims(claims);
        Collection<GrantedAuthority> authorities = extractAuthorities(claims);

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }

    private String getUsernameFromClaims(Claims claims) {
        return claims.getSubject();
    }

    private Collection<GrantedAuthority> extractAuthorities(Claims claims) {
        return authoritiesExtractor.extractAuthorities(claims);
    }

    private void logSuccessfulAuthentication(Authentication authResult) {
        log.debug(AuthConstants.LOGGER_SUCCESS_AUTHENTICATION, authResult.getName());
    }

    private void logFailedAuthentication(String token, Throwable error) {
        log.warn(AuthConstants.LOGGER_FAILED_AUTHENTICATION, token, error.getMessage());
    }

    private Mono<Authentication> handleAuthenticationError(Throwable error) {
        log.warn(AuthConstants.LOGGER_AUTHENTICATION_FAILED, error.getMessage());
        if (error instanceof ExpiredJwtException) {
            return Mono.error(new TokenValidationException(AuthConstants.TOKEN_EXPIRED, error));
        }
        return Mono.error(new InvalidCredentialsException(AuthConstants.INVALID_JWT_TOKEN,error));
    }
}