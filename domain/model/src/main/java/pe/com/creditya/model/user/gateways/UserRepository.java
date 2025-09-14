package pe.com.creditya.model.user.gateways;

import pe.com.creditya.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserRepository {
    Mono<User> saveUser(User user);

    Mono<Boolean> existsByEmail(String email);

    Mono<User> findByDocumentNumber(String documentNumber);

    Mono<User> findByEmail(String email);

    Flux<User> findByEmails(List<String> emails);

}
