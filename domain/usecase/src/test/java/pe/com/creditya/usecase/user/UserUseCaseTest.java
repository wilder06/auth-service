package pe.com.creditya.usecase.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.com.creditya.model.exceptions.TechnicalException;
import pe.com.creditya.model.exceptions.UserAlreadyExistsException;
import pe.com.creditya.model.user.User;
import pe.com.creditya.model.user.gateways.UserRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserUseCaseTest {

    private UserRepository userRepository;
    private UserUseCase userUseCase;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userUseCase = new UserUseCase(userRepository);
        user = User.builder()
                .name("demo")
                .lastName("pablo")
                .email("demo@gmail.com")
                .baseSalary(BigDecimal.valueOf(10.0))
                .address("direccion")
                .phoneNumber("+516895987")
                .birthDate(LocalDate.now())
                .build();
    }

    @Test
    void saveUser_success_whenUserNotExists() {

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.saveUser(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).saveUser(user);
    }

    @Test
    void saveUser_error_whenUserAlreadyExists() {

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof UserAlreadyExistsException &&
                                throwable.getMessage().contains(user.getEmail()))
                .verify();

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository, never()).saveUser(any());
    }

    @Test
    void saveUser_error_whenUnexpectedErrorOccurs() {

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(Mono.error(new RuntimeException("DB connection lost")));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof TechnicalException &&
                                throwable.getMessage().contains("Error inesperado en registro de usuario"))
                .verify();

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository, never()).saveUser(any());
    }
}