package pe.com.creditya.usecase.user;

import pe.com.creditya.model.user.User;
import reactor.core.publisher.Mono;

public interface IUserUseCase {
    Mono<User> saveUser(User user);
}
