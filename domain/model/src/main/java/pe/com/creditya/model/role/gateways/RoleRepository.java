package pe.com.creditya.model.role.gateways;

import pe.com.creditya.model.role.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Role> findByIdRole(Long idRole);
}
