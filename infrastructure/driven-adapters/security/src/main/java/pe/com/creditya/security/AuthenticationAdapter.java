package pe.com.creditya.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pe.com.creditya.model.common.exceptions.InvalidCredentialsException;
import pe.com.creditya.model.role.Role;
import pe.com.creditya.model.token.gateways.TokenRepository;
import pe.com.creditya.model.user.User;
import pe.com.creditya.r2dbc.RoleReactiveRepositoryAdapter;
import pe.com.creditya.r2dbc.UserReactiveRepositoryAdapter;
import pe.com.creditya.security.common.constants.Constants;
import pe.com.creditya.model.common.exceptions.RoleNotFoundException;
import pe.com.creditya.security.config.PasswordEncoderAdapter;
import pe.com.creditya.security.jwt.JwtProvider;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticationAdapter implements TokenRepository {

    private final UserReactiveRepositoryAdapter userRepository;
    private final RoleReactiveRepositoryAdapter roleRepository;
    private final PasswordEncoderAdapter passwordEncoderAdapter;
    private final JwtProvider jwtProvider;

    @Override
    public Mono<String> generateToken(String email, String password) {
        return findUserByEmail(email)
                .flatMap(user -> validatePassword(password, user))
                .flatMap(this::generateUserToken)
                .doOnSuccess(token -> log.debug(Constants.LOGGER_TOKEN_GENERATED_SUCCESS, email))
                .doOnError(error -> handleTokenGenerationError(error, email));
    }

    private Mono<User> findUserByEmail(String email) {
        String normalizedEmail = email.toLowerCase(Locale.ROOT);
        return userRepository.findByEmail(normalizedEmail)
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn(Constants.LOGGER_USER_NOT_FOUND, normalizedEmail);
                    return Mono.error(new InvalidCredentialsException(Constants.INVALID_CREDENTIALS));
                }))
                .doOnNext(user -> log.debug(Constants.LOGGER_USER_FOUND, user.getId()));
    }

    private Mono<User> validatePassword(String password, User user) {
        return Mono.fromCallable(() -> passwordEncoderAdapter.matches(password, user.getPassword()))
                .flatMap(matches -> {
                    if (!matches) {
                        log.warn(Constants.LOGGER_INVALID_PASSWORD, user.getEmail());
                        return Mono.error(new InvalidCredentialsException(Constants.INVALID_CREDENTIALS));
                    }
                    log.debug(Constants.LOGGER_PASSWORD_VALIDATED, user.getEmail());
                    return Mono.just(user);
                });
    }

    private Mono<String> generateUserToken(User user) {
        return findUserRole(user.getIdRole())
                .map(role -> createUserDetails(user, role))
                .map(userDetails -> jwtProvider.generateToken(userDetails, user))
                .doOnNext(token -> log.debug(Constants.LOGGER_TOKEN_CREATED, user.getEmail()));
    }

    private Mono<Role> findUserRole(Long roleId) {
        return roleRepository.findByIdRole(roleId)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error(Constants.LOGGER_ROLE_NOT_FOUND, roleId);
                    return Mono.error(new RoleNotFoundException(Constants.LOGGER_ROLE_NOT_FOUND));
                }));
    }

    private UserDetails createUserDetails(User user, Role role) {
        var authorities = List.of(new SimpleGrantedAuthority(Constants.PREFIX_TOKEN + role.getName().toUpperCase()));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    private void handleTokenGenerationError(Throwable error, String email) {
        if (error instanceof InvalidCredentialsException) {
            log.warn(Constants.LOGGER_AUTHENTICATION_FAILED, email, error.getMessage());
        } else if (error instanceof RoleNotFoundException) {
            log.error(Constants.LOGGER_ROLE_ERROR, email, error.getMessage());
        } else {
            log.error(Constants.LOGGER_TOKEN_GENERATION_ERROR, email, error.getMessage(), error);
        }
    }
}

