package pe.com.creditya.r2dbc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import pe.com.creditya.model.user.User;
import pe.com.creditya.r2dbc.entity.UserEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @InjectMocks
    UserReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    TransactionalOperator transactionalOperator;

    @Mock
    ObjectMapper mapper;

    private final UserEntity userEntity = UserEntity.builder()
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
            .birthDate(LocalDate.now())
            .build();

    @Test
    void saveUser_shouldSuccess() {
        when(mapper.map(userEntity, User.class)).thenReturn(user);
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);
        when(repository.save(userEntity)).thenReturn(Mono.just(userEntity));
        when(transactionalOperator.transactional(any(Mono.class))).then(invocation -> invocation.getArgument(0));

        StepVerifier.create(repositoryAdapter.saveUser(user))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void findUserByEmail_shouldReturnTrue() {
        String email = "test@example.com";
        when(repository.findByEmail(email)).thenReturn(Mono.just(UserEntity.builder().build()));

        Mono<Boolean> result = repositoryAdapter.existsByEmail(email);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void findUsersByEmails_shouldReturnSuccess() {
        List<String> emails = List.of("test@example.com");
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        when(repository.findByEmailIn(emails)).thenReturn(Flux.just(userEntity));

        StepVerifier.create(repositoryAdapter.findByEmails(emails))
                .assertNext(response -> {
                    assertThat(response.getEmail()).isEqualTo("demo@gmail.com");
                })
                .verifyComplete();
    }

    @Test
    void findUserByDocumentNumber_shouldReturnSuccess() {
        String documentNumber = "12345678";
        when(repository.findByDocumentNumber(documentNumber)).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        StepVerifier.create(repositoryAdapter.findByDocumentNumber(documentNumber))
                .assertNext(response -> {
                    assertThat(response.getEmail()).isEqualTo("demo@gmail.com");
                })
                .verifyComplete();
    }

    @Test
    void findUserByEmail_shouldReturnSuccess() {
        String email = "demo@gmail.com";
        when(repository.findByEmail(email)).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        StepVerifier.create(repositoryAdapter.findByEmail(email))
                .assertNext(response -> {
                    assertThat(response.getEmail()).isEqualTo("demo@gmail.com");
                })
                .verifyComplete();
    }
}