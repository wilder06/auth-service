package pe.com.creditya.model.user.gateways;

import pe.com.creditya.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> saveUser(User user);

    Mono<Boolean> existsByEmail(String email);

    Mono<User>findByDocumentNumber(String documentNumber);
}
