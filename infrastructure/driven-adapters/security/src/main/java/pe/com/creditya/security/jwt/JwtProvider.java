package pe.com.creditya.security.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pe.com.creditya.model.user.User;
import pe.com.creditya.security.common.constants.Constants;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final KeyPair keyPair;

    public String generateToken(UserDetails userDetails, User user) {
        long expiryMillis = jwtProperties.getExpiration() * 60L * 1000L;
        Instant now = Instant.now();
        Instant exp = now.plusMillis(expiryMillis);

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(Constants.PREFIX_ROLES, roles)
                .claim(Constants.USER_ID, user.getDocumentNumber())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(keyPair.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException ex) {
            log.debug(Constants.LOGGER_EXPIRED_TOKEN, ex);
            throw ex;
        }
    }

    public Mono<Claims> validateTokenAndGetClaims(String token) {
        return Mono.fromCallable(() -> parseClaims(token))
                .onErrorResume(e -> Mono.error(new BadCredentialsException(Constants.INVALID_JWT_TOKEN, e)));
    }

}


