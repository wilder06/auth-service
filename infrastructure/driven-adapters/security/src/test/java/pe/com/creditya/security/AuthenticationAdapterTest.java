package pe.com.creditya.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.com.creditya.model.role.Role;
import pe.com.creditya.model.user.User;
import pe.com.creditya.r2dbc.RoleReactiveRepositoryAdapter;
import pe.com.creditya.r2dbc.UserReactiveRepositoryAdapter;
import pe.com.creditya.security.config.PasswordEncoderAdapter;
import pe.com.creditya.security.jwt.JwtProvider;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationAdapterTest {

    @Mock
    private UserReactiveRepositoryAdapter userRepository;

    @Mock
    private RoleReactiveRepositoryAdapter roleRepository;

    @Mock
    private PasswordEncoderAdapter passwordEncoderAdapter;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthenticationAdapter authenticationAdapter;

    @Test
    void generateToken_Success() {

        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        String token = "generated-jwt-token";

        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setIdRole(1L);

        Role role = new Role();
        role.setId(2L);
        role.setName("admin");

        when(userRepository.findByEmail(email.toLowerCase())).thenReturn(Mono.just(user));
        when(passwordEncoderAdapter.matches(password, encodedPassword)).thenReturn(true);
        when(roleRepository.findByIdRole(user.getIdRole())).thenReturn(Mono.just(role));
        when(jwtProvider.generateToken(any(), any())).thenReturn(token);

        StepVerifier.create(authenticationAdapter.generateToken(email, password))
                .expectNext(token)
                .verifyComplete();
    }

}