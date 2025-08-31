package pe.com.creditya.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.com.creditya.api.common.UserPath;
import pe.com.creditya.api.common.ValidatorConfig;
import pe.com.creditya.api.dtos.UserRequest;
import pe.com.creditya.api.dtos.UserResponse;
import pe.com.creditya.api.mapper.UserMapper;
import pe.com.creditya.api.mapper.UserMapperImpl;
import pe.com.creditya.model.user.User;
import pe.com.creditya.usecase.user.IUserUseCase;
import pe.com.creditya.usecase.user.UserUseCase;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, ValidatorConfig.class, UserMapperImpl.class})
@EnableConfigurationProperties(UserPath.class)
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private IUserUseCase userUseCase;

    @Autowired
    private UserPath userPath;

    private final UserResponse userResponse = UserResponse.builder()
            .name("demo")
            .lastName("pablo")
            .email("demo@gmail.com")
            .baseSalary(BigDecimal.valueOf(10.0))
            .address("direccion")
            .phoneNumber("+516895987")
            .birthDate(LocalDate.now())
            .build();
    private final User userOne = User.builder()
            .name("demo")
            .lastName("pablo")
            .email("demo@gmail.com")
            .baseSalary(BigDecimal.valueOf(10.0))
            .address("direccion")
            .phoneNumber("+516895987")
            .birthDate(LocalDate.now())
            .build();
    private final UserRequest userRequest = UserRequest.builder()
            .name("demo")
            .lastName("pablo")
            .email("demo@gmail.com")
            .baseSalary(BigDecimal.valueOf(10.0))
            .address("direccion")
            .phoneNumber("+516895987")
            .birthDate(LocalDate.now())
            .build();

    @Test
    void shouldLoadUserPathProperties() {
        assertEquals("/api/v1/usuarios", userPath.getUsers());
    }

    @Test
    void shouldReturn200WhenSaveUser() {
        when(userMapper.toUser(any(UserRequest.class))).thenReturn(userOne);
        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.just(userOne));
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);
        webTestClient.post()
                .uri(userPath.getUsers())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().isCreated();
    }
}