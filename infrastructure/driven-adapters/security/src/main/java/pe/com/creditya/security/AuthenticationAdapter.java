package pe.com.creditya.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pe.com.creditya.model.token.gateways.TokenRepository;
import pe.com.creditya.r2dbc.RoleReactiveRepositoryAdapter;
import pe.com.creditya.r2dbc.UserReactiveRepositoryAdapter;
import pe.com.creditya.security.common.constants.Constants;
import pe.com.creditya.model.common.exceptions.InvalidCredentialsException;
import pe.com.creditya.model.common.exceptions.RoleNotFoundException;
import pe.com.creditya.security.config.PasswordEncoderAdapter;
import pe.com.creditya.security.jwt.JwtProvider;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Locale;
@RequiredArgsConstructor
@Component
public class AuthenticationAdapter implements TokenRepository {

    private final UserReactiveRepositoryAdapter userRepository;
    private final RoleReactiveRepositoryAdapter roleRepository;
    private final PasswordEncoderAdapter passwordEncoderAdapter;
    private final JwtProvider jwtProvider;

    @Override
        public Mono<String> generateToken(String email, String password) {
            return userRepository.findByEmail(email.toLowerCase(Locale.ROOT))
                    .switchIfEmpty(Mono.error(new InvalidCredentialsException(Constants.INVALID_CREDENTIALS)))
                    .flatMap(user -> {
                        boolean matches = passwordEncoderAdapter.matches(password, user.getPassword());
                        if (!matches) {
                            return Mono.error(new InvalidCredentialsException(Constants.INVALID_CREDENTIALS));
                        }
                        return roleRepository.findByIdRole(user.getIdRole())
                                .switchIfEmpty(Mono.error(new RoleNotFoundException(Constants.ROLE_NOT_FOUND)))
                                .flatMap(role -> {
                                    var authorities = List.of(new SimpleGrantedAuthority(Constants.PREFIX_TOKEN + role.getName().toUpperCase()));
                                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                                            .withUsername(user.getEmail())
                                            .password(user.getPassword())
                                            .authorities(authorities)
                                            .build();
                                    String token = jwtProvider.generateToken(userDetails, user);
                                    return Mono.just(token);
                                });
                    });
        }
}

