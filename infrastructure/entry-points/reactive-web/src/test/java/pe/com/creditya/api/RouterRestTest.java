package pe.com.creditya.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.SecurityConfig;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.com.creditya.api.common.UserPath;
import pe.com.creditya.api.common.validators.ValidatorConfig;
import pe.com.creditya.api.dtos.UserRequest;
import pe.com.creditya.api.dtos.UserResponse;
import pe.com.creditya.api.mapper.UserMapper;
import pe.com.creditya.api.mapper.UserMapperImpl;
import pe.com.creditya.model.user.User;
import pe.com.creditya.usecase.user.IUserUseCase;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

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
            .documentNumber("32178987")
            .birthDate(LocalDate.now())
            .build();
    private final User userOne = User.builder()
            .name("demo")
            .lastName("pablo")
            .email("demo@gmail.com")
            .baseSalary(BigDecimal.valueOf(10.0))
            .address("direccion")
            .phoneNumber("+516895987")
            .documentNumber("32178987")
            .password("admin123")
            .idRole(3L)
            .birthDate(LocalDate.now())
            .build();
    private final UserRequest userRequest = UserRequest.builder()
            .name("demo")
            .lastName("pablo")
            .email("demo@gmail.com")
            .baseSalary(BigDecimal.valueOf(10.0))
            .address("direccion")
            .password("admin123")
            .idRole(3L)
            .phoneNumber("+516895987")
            .documentNumber("32178987")
            .birthDate(LocalDate.now())
            .build();

    @Test
    void shouldLoadUserPathProperties() {
        assertEquals("/api/v1/usuarios/register", userPath.getUsers());
        assertEquals("/api/v1/usuarios/{documentNumber}", userPath.getUserByDocumentNumber());
    }

    @Test
    void shouldReturn200WhenSaveUser() {
        when(userMapper.toUser(any(UserRequest.class))).thenReturn(userOne);
        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.just(userOne));
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);
        webTestClient
                .mutateWith(mockUser("admin").roles("ADMIN"))
                .mutateWith(csrf())
                .post()
                .uri(userPath.getUsers())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userRequest)
                .exchange()
                .expectStatus().isCreated();
    }
    @Test
    void testListenGET_UserExists_ReturnsTrue() {
        when(userUseCase.findByDocumentNumber("12345678"))
                .thenReturn(Mono.just(userOne));
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);


        webTestClient
                .mutateWith(mockUser("admin").roles("ADMIN"))
                .get()
                .uri(userPath.getUserByDocumentNumber(), "32178987")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(UserResponse.class)
                .value(response -> Assertions.assertThat(response.documentNumber()).isEqualTo("32178987"));
    }

}