package pe.com.creditya.security.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;

import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pe.com.creditya.security.common.constants.Constants;
import pe.com.creditya.security.jwt.JwtAuthenticationManager;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private final JwtAuthenticationManager authenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.error(new UnsupportedOperationException(Constants.MESSAGE_NOT_SUPPORTED));
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String token = exchange.getAttribute(Constants.TOKEN_ATTRIBUTE);
        if (token == null) {
            return Mono.empty();
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(token, token);
        return authenticationManager.authenticate(authToken)
                .map(SecurityContextImpl::new);
    }
}