package pe.com.creditya.r2dbc;

import org.springframework.transaction.reactive.TransactionalOperator;
import pe.com.creditya.model.user.User;
import pe.com.creditya.model.user.gateways.UserRepository;
import pe.com.creditya.r2dbc.entity.UserEntity;
import pe.com.creditya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

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
        return super.save(user).as(transactionalOperator::transactional);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return repository.findByEmail(email).hasElement();
    }
}