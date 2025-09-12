package pe.com.creditya.r2dbc;

import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import pe.com.creditya.model.role.Role;
import pe.com.creditya.model.role.gateways.RoleRepository;
import pe.com.creditya.r2dbc.common.constants.LoggerConstants;
import pe.com.creditya.r2dbc.entity.RoleEntity;
import pe.com.creditya.r2dbc.helper.ReactiveAdapterOperations;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class RoleReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Role,
        RoleEntity,
        Long,
        RoleReactiveRepository> implements RoleRepository {

    public RoleReactiveRepositoryAdapter(RoleReactiveRepository repository,
                                         ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Role.class));
    }


    @Override
    public Mono<Role> findByIdRole(Long id) {
        log.info(LoggerConstants.LOGGER_VERIFIED_ROLE, id);
        return super.findById(id)
                .doOnNext(role -> log.info(LoggerConstants.LOGGER_NOT_FOUND_ROLE, role));
    }
}

