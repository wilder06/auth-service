package pe.com.creditya.usecase.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.com.creditya.model.common.constants.LogConstants;
import pe.com.creditya.model.common.exceptions.NotFoundException;
import pe.com.creditya.model.common.exceptions.TechnicalException;
import pe.com.creditya.model.common.exceptions.UserAlreadyExistsException;
import pe.com.creditya.model.common.validations.UserValidator;
import pe.com.creditya.model.user.User;
import pe.com.creditya.model.user.gateways.PasswordEncoderRepository;
import pe.com.creditya.model.user.gateways.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserUseCaseTest {

    private UserRepository userRepository;
    private IUserUseCase userUseCase;
    private UserValidator validator;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        validator = mock(UserValidator.class);
        PasswordEncoderRepository passwordEncoderRepository = mock(PasswordEncoderRepository.class);
        userUseCase = new UserUseCase(userRepository, passwordEncoderRepository, validator);
        user = User.builder()
                .name("demo")
                .lastName("pablo")
                .email("demo@gmail.com")
                .baseSalary(BigDecimal.valueOf(10.0))
                .address("direccion")
                .phoneNumber("+516895987")
                .documentNumber("12345678")
                .birthDate(LocalDate.now())
                .build();
    }

    @Test
    void saveUser_shouldSuccessUserNotExists() {

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(Mono.just(false));
        when(userRepository.saveUser(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).saveUser(user);
    }

    @Test
    void saveUser_shouldUserAlreadyExists() {

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
    void findUserByDocumentNumber_shouldTechnicalException() {
        String documentNumber = "12345678";
        String errorMessage = "Database connection failed";

        when(userRepository.findByDocumentNumber(documentNumber))
                .thenReturn(Mono.error(new TechnicalException(errorMessage, null)));

        StepVerifier.create(userUseCase.findByDocumentNumber(documentNumber))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(TechnicalException.class, error);
                })
                .verify();
    }

    @Test
    void saveUser_shouldReturnTechnicalException() {
        doNothing().when(validator).validate(user);
        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(Mono.error(new TechnicalException("Repo failed", null)));
        Mono<User> result = userUseCase.saveUser(user);

        StepVerifier.create(result)
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(TechnicalException.class, error);
                    Assertions.assertEquals(LogConstants.LOGGER_ERROR_GENERAL, error.getMessage());
                })
                .verify();
    }

    @Test
    void findUsersByEmails_shouldTechnicalException() {
        List<String> emails = List.of("demo@example.com");
        String errorMessage = "Database connection failed";

        when(userRepository.findByEmails(emails))
                .thenReturn(Flux.error(new TechnicalException(errorMessage, null)));

        StepVerifier.create(userUseCase.findByEmails(emails))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(TechnicalException.class, error);
                })
                .verify();
    }

    @Test
    void findUsersByEmails_shouldNotFoundException() {
        List<String> emails = List.of("demo@example.com");
        when(userRepository.findByEmails(emails))
                .thenReturn(Flux.empty());

        StepVerifier.create(userUseCase.findByEmails(emails))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(NotFoundException.class, error);
                })
                .verify();
    }

    @Test
    void findUserByDocumentNumber_shouldNotFoundException() {
        when(userRepository.findByDocumentNumber(user.getDocumentNumber()))
                .thenReturn(Mono.empty());

        StepVerifier.create(userUseCase.findByDocumentNumber(user.getDocumentNumber()))
                .expectErrorSatisfies(error -> {
                    Assertions.assertInstanceOf(NotFoundException.class, error);
                })
                .verify();
    }

    @Test
    void findUserByDocumentNumber_shouldSuccessful() {

        when(userRepository.findByDocumentNumber(user.getDocumentNumber()))
                .thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.findByDocumentNumber(user.getDocumentNumber()))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).findByDocumentNumber(user.getDocumentNumber());
    }

    @Test
    void findUsersByEmails_shouldSuccessful() {
        List<String> emails = List.of("demo@example.com");

        when(userRepository.findByEmails(emails))
                .thenReturn(Flux.just(user));

        StepVerifier.create(userUseCase.findByEmails(emails))
                .expectNext(user)
                .verifyComplete();
    }
}