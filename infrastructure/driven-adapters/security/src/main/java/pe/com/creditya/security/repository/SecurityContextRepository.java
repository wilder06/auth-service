package pe.com.creditya.security.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;

import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pe.com.creditya.security.common.constants.Constants;
import pe.com.creditya.security.jwt.JwtAuthenticationManager;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
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
        return extractTokenFromRequest(exchange.getRequest())
                .flatMap(token -> authenticateAndCreateContext(token)
                        .doOnSubscribe(sub -> log.debug(Constants.LOGGER_AUTH_STARTED, token))
                        .doOnSuccess(context -> log.debug(Constants.LOGGER_AUTH_SUCCESS, context.getAuthentication().getName()))
                        .doOnError(error -> log.warn(Constants.LOGGER_AUTH_FAILED, token, error.getMessage())))
                .onErrorResume(error -> {
                    log.debug(Constants.LOGGER_LOAD_OPERATION_FAILED, exchange.getRequest().getURI(), error.getMessage());
                    return Mono.error(error);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug(Constants.LOGGER_NO_TOKEN_PRESENT, exchange.getRequest().getURI());
                    return Mono.empty();
                }));
    }

    private Mono<SecurityContext> authenticateAndCreateContext(String token) {
        return Mono.just(createAuthToken(token))
                .flatMap(authenticationManager::authenticate)
                .map(this::createSecurityContext);
    }

    private UsernamePasswordAuthenticationToken createAuthToken(String token) {
        return new UsernamePasswordAuthenticationToken(token, token);
    }

    private SecurityContext createSecurityContext(Authentication authentication) {
        SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(authentication);
        return context;
    }

    private Mono<String> extractTokenFromRequest(ServerHttpRequest request) {
        return Mono.fromSupplier(() -> extractToken(request))
                .onErrorResume(error -> {
                    log.debug(Constants.LOGGER_TOKEN_EXTRACTION_ERROR, request.getURI(), error.getMessage());
                    return Mono.empty();
                });
    }

    private String extractToken(ServerHttpRequest request) {
        String tokenFromHeader = extractTokenFromHeader(request);
        if (tokenFromHeader != null) {
            log.debug(Constants.LOGGER_TOKEN_FOUND_HEADER, request.getURI());
            return tokenFromHeader;
        }

        String tokenFromQuery = extractTokenFromQuery(request);
        if (tokenFromQuery != null) {
            log.debug(Constants.LOGGER_TOKEN_FOUND_QUERY, request.getURI());
            return tokenFromQuery;
        }

        log.debug(Constants.LOGGER_TOKEN_NOT_FOUND, request.getURI());
        return null;
    }

    private String extractTokenFromHeader(ServerHttpRequest request) {
        return Optional.ofNullable(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .filter(header -> header.startsWith(Constants.PREFIX_BEARER))
                .map(header -> header.substring(Constants.PREFIX_BEARER.length()))
                .orElse(null);
    }

    private String extractTokenFromQuery(ServerHttpRequest request) {
        return Optional.ofNullable(request.getQueryParams().getFirst(Constants.TOKEN_ATTRIBUTE))
                .filter(token -> !token.isBlank())
                .map(String::trim)
                .orElse(null);
    }

}