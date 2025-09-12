package pe.com.creditya.usecase.auth;

import reactor.core.publisher.Mono;

public interface IAuthUseCase {
    Mono<String> authenticate(String email, String password);
}
