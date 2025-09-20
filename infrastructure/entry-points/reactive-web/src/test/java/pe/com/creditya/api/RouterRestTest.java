package pe.com.creditya.api;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.com.creditya.api.common.UserPath;
import pe.com.creditya.api.common.validators.ValidatorConfig;
import pe.com.creditya.api.dtos.UserRequest;
import pe.com.creditya.api.dtos.UserResponse;
import pe.com.creditya.api.mapper.UserMapper;
import pe.com.creditya.api.mapper.UserMapperImpl;
import pe.com.creditya.model.role.RoleEnum;
import pe.com.creditya.model.user.User;
import pe.com.creditya.usecase.user.IUserUseCase;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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
            .role(RoleEnum.USER.name())
            .phoneNumber("+516895987")
            .documentNumber("32178987")
            .birthDate(LocalDate.now())
            .build();

    @Test
    void saveUser_shouldLoadUserPathProperties() {
        assertEquals("/api/v1/usuarios", userPath.getUsers());
    }

    @Test
    void saveUser_shouldReturnSuccess() {
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
    void findUserByDocumentNumber_shouldReturnSuccess() {
        when(userUseCase.findByDocumentNumber("32178987"))
                .thenReturn(Mono.just(userOne));
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);

        webTestClient
                .mutateWith(mockUser()
                        .authorities(new SimpleGrantedAuthority("ADMIN")))
                .mutateWith(csrf())
                .get()
                .uri(userPath.getUserByDocumentNumber(), "32178987")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody(UserResponse.class)
                .value(response -> Assertions.assertThat(response.documentNumber()).isEqualTo("32178987"));
    }

    @Test
    void findUsersByEmails_shouldReturnSuccess() {
        when(userUseCase.findByEmails(List.of("jn@example.com")))
                .thenReturn(Flux.just(userOne));
        when(userMapper.toUserResponse(any(User.class))).thenReturn(userResponse);


        webTestClient
                .mutateWith(mockUser()
                        .authorities(new SimpleGrantedAuthority("ADVISOR")))
                .mutateWith(csrf())
                .post()
                .uri(userPath.getUsersByEmails())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of("jn@example.com"))
                .exchange()
                .expectStatus().isOk();

    }

}