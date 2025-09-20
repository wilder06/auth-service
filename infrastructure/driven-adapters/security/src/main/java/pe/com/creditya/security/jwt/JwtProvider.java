package pe.com.creditya.security.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pe.com.creditya.model.common.exceptions.TokenGenerationException;
import pe.com.creditya.model.common.exceptions.TokenValidationException;
import pe.com.creditya.model.user.User;
import pe.com.creditya.security.common.constants.AuthConstants;
import pe.com.creditya.security.common.constants.Constants;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtProperties;
    private final KeyPair keyPair;

    public String generateToken(UserDetails userDetails, User user) {
        try {
            long expiryMillis = calculateExpiryMillis();
            Instant now = getCurrentInstant();
            Instant exp = calculateExpiration(now, expiryMillis);

            List<String> roles = extractUserRoles(userDetails);

            return buildJwtToken(userDetails, user, roles, now, exp);

        } catch (JwtException jwtException) {
            log.error(AuthConstants.LOGGER_TOKEN_GENERATION_ERROR, userDetails.getUsername(), jwtException.getMessage());
            throw new TokenGenerationException(AuthConstants.TOKEN_GENERATION_ERROR, jwtException);
        } catch (Exception unexpectedException) {
            log.error(AuthConstants.LOGGER_UNEXPECTED_ERROR, userDetails.getUsername(), unexpectedException.getMessage());
            throw new TokenGenerationException(AuthConstants.UNEXPECTED_ERROR, unexpectedException);
        }
    }

    public Claims parseClaims(String token) {
        try {
            return buildJwtParser()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException expiredException) {
            log.debug(Constants.LOGGER_EXPIRED_TOKEN, expiredException.getMessage());
            throw new TokenValidationException(AuthConstants.TOKEN_EXPIRED, expiredException);

        } catch (JwtException jwtException) {
            log.debug(AuthConstants.LOGGER_TOKEN_PARSING_ERROR, token, jwtException.getMessage());
            throw new TokenValidationException(AuthConstants.TOKEN_PARSING_ERROR, jwtException);
        }
    }

    public Mono<Claims> validateTokenAndGetClaims(String token) {
        return Mono.fromCallable(() -> parseClaims(token))
                .onErrorResume(this::handleValidationError);
    }

    private long calculateExpiryMillis() {
        return jwtProperties.getExpiration() * 60L * 1000L;
    }

    private Instant getCurrentInstant() {
        return Instant.now();
    }

    private Instant calculateExpiration(Instant now, long expiryMillis) {
        return now.plusMillis(expiryMillis);
    }

    private List<String> extractUserRoles(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    private JwtParser buildJwtParser() {
        return Jwts.parser()
                .verifyWith(keyPair.getPublic())
                .build();
    }

    private String buildJwtToken(UserDetails userDetails, User user, List<String> roles, Instant now, Instant exp) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(Constants.PREFIX_ROLES, roles)
                .claim(Constants.USER_ID, user.getDocumentNumber())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    private Mono<Claims> handleValidationError(Throwable error) {
        if (error instanceof TokenValidationException) {
            return Mono.error(error);
        }

        log.warn(AuthConstants.LOGGER_VALIDATION_ERROR, error.getMessage());
        return Mono.error(new BadCredentialsException(Constants.INVALID_JWT_TOKEN, error));
    }
}



