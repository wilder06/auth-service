package pe.com.creditya.usecase.user;

import lombok.RequiredArgsConstructor;
import pe.com.creditya.model.common.constants.LogConstants;
import pe.com.creditya.model.common.exceptions.NotFoundException;
import pe.com.creditya.model.common.exceptions.TechnicalException;
import pe.com.creditya.model.common.exceptions.UserAlreadyExistsException;
import pe.com.creditya.model.user.User;
import pe.com.creditya.model.user.gateways.PasswordEncoderRepository;
import pe.com.creditya.model.user.gateways.UserRepository;
import pe.com.creditya.model.common.validations.UserValidator;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements IUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoderRepository passwordEncoderRepository;
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
                    return Mono.error(new TechnicalException(LogConstants.LOGGER_ERROR_GENERAL, ex));
                });
    }

    private Mono<User> handleUserExistence(boolean exists, User user) {
        if (exists) {
            return Mono.error(new UserAlreadyExistsException(user.getEmail()));
        }
        return Mono.just(user)
                .map(newUser -> {
                    String encodedPassword = passwordEncoderRepository.encode(newUser.getPassword());
                    newUser.setPassword(encodedPassword);
                    return newUser;
                })
                .flatMap(userRepository::saveUser);
    }

    @Override
    public Mono<User> findByDocumentNumber(String documentNumber) {
        return userRepository.findByDocumentNumber(documentNumber)
                .onErrorResume(ex -> {
                    if (ex instanceof UserAlreadyExistsException) {
                        return Mono.error(new NotFoundException(LogConstants.LOGGER_USER_NOT_EXISTS + documentNumber));
                    }
                    return Mono.error(new TechnicalException(ex.getMessage(), ex));
                });
    }
}