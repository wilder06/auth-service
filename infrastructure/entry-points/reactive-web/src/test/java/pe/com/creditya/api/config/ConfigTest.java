package pe.com.creditya.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pe.com.creditya.api.Handler;
import pe.com.creditya.api.RouterRest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.com.creditya.api.common.UserPath;
import pe.com.creditya.api.common.validators.ValidatorConfig;
import pe.com.creditya.api.dtos.UserRequest;
import pe.com.creditya.api.dtos.UserResponse;
import pe.com.creditya.api.mapper.UserMapper;
import pe.com.creditya.api.mapper.UserMapperImpl;
import pe.com.creditya.model.user.User;
import pe.com.creditya.usecase.user.UserUseCase;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, UserPath.class,
        ValidatorConfig.class, UserMapperImpl.class })
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private UserUseCase userUseCase;

    private final UserResponse userResponse =  UserResponse.builder()
            .name("demo")
            .lastName("pablo")
            .email("demo@gmail.com")
            .baseSalary(BigDecimal.valueOf(10.0))
            .address("direccion")
            .phoneNumber("+516895987")
            .birthDate(LocalDate.now())
            .build();
    private final User user = User.builder()
            .name("demo")
            .lastName("pablo")
            .email("demo@gmail.com")
            .baseSalary(BigDecimal.valueOf(10.0))
            .address("direccion")
            .phoneNumber("+516895987")
            .documentNumber("32178987")
            .birthDate(LocalDate.now())
            .build();

    @BeforeEach
    void setUp() {
        when(userMapper.toUser(any(UserRequest.class))).thenReturn(user);
        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.just(user));
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

    }

    @Test
    void corsConfigurationShouldAllowOrigins() {
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .exchange()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin")
                .expectStatus().isOk();
    }

}