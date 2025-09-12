package pe.com.creditya.r2dbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.reactive.TransactionalOperator;
import pe.com.creditya.model.common.exceptions.NotFoundException;
import pe.com.creditya.model.common.exceptions.UserPersistenceException;
import pe.com.creditya.model.user.User;
import pe.com.creditya.model.user.gateways.UserRepository;
import pe.com.creditya.r2dbc.common.constants.LoggerConstants;
import pe.com.creditya.r2dbc.entity.UserEntity;
import pe.com.creditya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        String,
        UserReactiveRepository
        > implements UserRepository {
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, entity -> mapper.map(entity, User.class));
        this.transactionalOperator = transactionalOperator;
    }

    private final TransactionalOperator transactionalOperator;

    @Override
    public Mono<User> saveUser(User user) {
        log.info(LoggerConstants.LOG_SAVE_USER, user);

        return super.save(user)
                .as(transactionalOperator::transactional)
                .onErrorResume(ex ->
                        Mono.error(new UserPersistenceException(
                                LoggerConstants.LOG_SAVE_USER_FAIL + user.getEmail(),
                                ex
                        )));
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        log.info(LoggerConstants.LOG_START_VERIFY_EXIST_EMAIL, email);
        return repository.findByEmail(email)
                .map(user -> true)
                .defaultIfEmpty(false)
                .onErrorResume(ex -> {
                    log.error("Error checking email {}: {}", email, ex.getMessage());
                    return Mono.just(false);
                });
    }

    @Override
    public Mono<User> findByDocumentNumber(String documentNumber) {
        log.info(LoggerConstants.LOG_START_VERIFY_EXIST_DOCUMENT, documentNumber);
        return repository.findByDocumentNumber(documentNumber)
                .doOnSubscribe(sub -> log.info("Ejecutando query..."))
                .doOnNext(user -> log.info("Usuario encontrado: {}", user))
                .doOnError(error -> log.error("Error en query: {}", error.getMessage()))
                .switchIfEmpty(Mono.fromRunnable(() -> log.info("No se encontró usuario")))
                .map(this::toEntity);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        log.info("Checking if user exists with email {}", email);

        return repository.findByEmail(email).map(this::toEntity)
                .doOnNext(user -> log.info("User found: {}", user));
    }

}