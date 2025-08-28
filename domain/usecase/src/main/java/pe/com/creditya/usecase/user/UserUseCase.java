package pe.com.creditya.usecase.user;

import lombok.RequiredArgsConstructor;
import pe.com.creditya.model.constants.UserConstants;
import pe.com.creditya.model.exceptions.TechnicalException;
import pe.com.creditya.model.exceptions.UserAlreadyExistsException;
import pe.com.creditya.model.user.User;
import pe.com.creditya.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements IUserUseCase{
    private final UserRepository userRepository;

    @Override
    public Mono<User> saveUser(User user) {
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists ->
                        handleUserExistence(exists, user))
                .onErrorResume(ex -> {
                    if (ex instanceof UserAlreadyExistsException) {
                        return Mono.error(new UserAlreadyExistsException(user.getEmail()));
                    }
                    return Mono.error(new TechnicalException(UserConstants.LOGGER_ERROR_GENERAL, ex.getCause()));
                });
    }


    private Mono<User> handleUserExistence(boolean exists, User user) {
        if (exists) {
            return Mono.error(new UserAlreadyExistsException(user.getEmail()));
        }
        return userRepository.saveUser(user);
    }
}