package pe.com.creditya.usecase.user;

import lombok.RequiredArgsConstructor;
import pe.com.creditya.model.common.constants.LogConstants;
import pe.com.creditya.model.common.exceptions.TechnicalException;
import pe.com.creditya.model.common.exceptions.UserAlreadyExistsException;
import pe.com.creditya.model.user.User;
import pe.com.creditya.model.user.gateways.UserRepository;
import pe.com.creditya.model.common.validations.UserValidator;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements IUserUseCase {
    private final UserRepository userRepository;
    private final UserValidator validator;

    @Override
    public Mono<User> saveUser(User user) {
        validator.validate(user);
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists ->
                        handleUserExistence(exists, user))
                .onErrorResume(ex -> {
                    if (ex instanceof UserAlreadyExistsException) {
                        return Mono.error(new UserAlreadyExistsException(user.getEmail()));
                    }
                    return Mono.error(new TechnicalException(LogConstants.LOGGER_ERROR_GENERAL, ex.getCause()));
                });
    }

    private Mono<User> handleUserExistence(boolean exists, User user) {
        if (exists) {
            return Mono.error(new UserAlreadyExistsException(user.getEmail()));
        }
        return userRepository.saveUser(user);
    }
}