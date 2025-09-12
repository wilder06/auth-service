package pe.com.creditya.usecase.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.com.creditya.model.token.gateways.TokenRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private TokenRepository authTokenRepository;

    @InjectMocks
    private AuthUseCase authUseCase;

    @Test
    void shouldReturnTokenWhenRepositoryGeneratesTokenSuccessfully() {
        String email = "user@example.com";
        String password = "securePassword123";
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xxxxx";

        when(authTokenRepository.generateToken(email, password))
                .thenReturn(Mono.just(expectedToken));

        StepVerifier.create(authUseCase.authenticate(email, password))
                .expectNext(expectedToken)
                .verifyComplete();
        verify(authTokenRepository, times(1)).generateToken(email, password);
    }
}