package pe.com.creditya.usecase.user;

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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserUseCaseTest {

    private UserRepository userRepository;
    private IUserUseCase userUseCase;
    private  UserValidator validator;
    private PasswordEncoderRepository passwordEncoderRepository;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
            validator = mock(UserValidator.class);
        passwordEncoderRepository=mock(PasswordEncoderRepository.class);
        userUseCase = new UserUseCase(userRepository,passwordEncoderRepository,validator);
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
    void findByDocumentNumber_returnsNotFoundException_whenUserAlreadyExistsExceptionThrown() {
        String documentNumber = "12345678";
        String expectedMessage = LogConstants.LOGGER_USER_NOT_EXISTS + documentNumber;

        when(userRepository.findByDocumentNumber(documentNumber))
                .thenReturn(Mono.error(new UserAlreadyExistsException("User already exists")));

        StepVerifier.create(userUseCase.findByDocumentNumber(documentNumber))
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException &&
                                throwable.getMessage().equals(expectedMessage))
                .verify();
    }
    @Test
    void findByDocumentNumber_returnsTechnicalException_whenGenericExceptionThrown() {
        String documentNumber = "12345678";
        String errorMessage = "Database connection failed";

        when(userRepository.findByDocumentNumber(documentNumber))
                .thenReturn(Mono.error(new RuntimeException(errorMessage)));

        StepVerifier.create(userUseCase.findByDocumentNumber(documentNumber))
                .expectErrorMatches(throwable ->
                        throwable instanceof TechnicalException &&
                                throwable.getMessage().equals(errorMessage) &&
                                throwable.getCause() instanceof RuntimeException)
                .verify();
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
    @Test
    void findUser_whenUserSentDocumentNumber() {

        when(userRepository.findByDocumentNumber(user.getDocumentNumber()))
                .thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.findByDocumentNumber(user.getDocumentNumber()))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).findByDocumentNumber(user.getDocumentNumber());
    }
}