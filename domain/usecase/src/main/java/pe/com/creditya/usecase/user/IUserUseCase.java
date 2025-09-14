package pe.com.creditya.usecase.user;

import pe.com.creditya.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface IUserUseCase {
    Mono<User> saveUser(User user);
   Mono<User> findByDocumentNumber(String documentNumber);
   Flux<User> findByEmails(List<String> emails);
}
