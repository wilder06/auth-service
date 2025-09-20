package pe.com.creditya.security.jwt;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import pe.com.creditya.security.common.constants.AuthConstants;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AuthoritiesExtractor {

    public Collection<GrantedAuthority> extractAuthorities(Claims claims) {
        try {
            List<String> roles = extractRolesFromClaims(claims);
            return mapRolesToAuthorities(roles);

        } catch (Exception exception) {
            log.warn(AuthConstants.LOGGER_ERROR_AUTHORITIES, exception.getMessage());
            return getDefaultAuthorities();
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> extractRolesFromClaims(Claims claims) {
        return claims.get(AuthConstants.PREFIX_ROLES, List.class);
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            logMissingRolesWarning();
            return getDefaultAuthorities();
        }

        return roles.stream()
                .map(this::normalizeRole)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private String normalizeRole(String role) {
        return role.startsWith(AuthConstants.PREFIX_TOKEN) ? role : AuthConstants.PREFIX_TOKEN + role;
    }

    private void logMissingRolesWarning() {
        log.warn(AuthConstants.LOGGER_ERROR_JWT_USER);
    }

    private Collection<GrantedAuthority> getDefaultAuthorities() {
        return List.of(new SimpleGrantedAuthority(AuthConstants.ROLE_USER));
    }
}