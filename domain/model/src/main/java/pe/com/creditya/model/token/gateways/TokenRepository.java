package pe.com.creditya.model.token.gateways;

import reactor.core.publisher.Mono;

public interface TokenRepository {
    Mono<String> generateToken(String email, String password);
}
