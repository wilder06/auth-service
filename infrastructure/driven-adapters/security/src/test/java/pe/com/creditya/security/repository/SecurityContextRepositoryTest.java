package pe.com.creditya.security.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.server.ServerWebExchange;
import pe.com.creditya.security.jwt.JwtAuthenticationManager;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityContextRepositoryTest {
    @Mock
    private JwtAuthenticationManager authenticationManager;

    @InjectMocks
    private SecurityContextRepository securityContextRepository;

    @Test
    void save_ShouldReturnUnsupportedOperationError() {
        // Arrange
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        SecurityContext context = new SecurityContextImpl();

        // Act & Assert
        StepVerifier.create(securityContextRepository.save(exchange, context))
                .expectErrorMatches(throwable -> throwable instanceof UnsupportedOperationException &&
                        throwable.getMessage().equals("Save not supported"))
                .verify();
    }

    @Test
    void load_WithBearerToken_ShouldReturnSecurityContext() {
        // Arrange
        String token = "valid-jwt-token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        Authentication authentication = new UsernamePasswordAuthenticationToken("user", null);
        SecurityContext expectedContext = new SecurityContextImpl();
        expectedContext.setAuthentication(authentication);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(authentication));

        // Act & Assert
        StepVerifier.create(securityContextRepository.load(exchange))
                .expectNextMatches(context ->
                        context.getAuthentication() != null &&
                                context.getAuthentication().equals(authentication))
                .verifyComplete();

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void load_WithQueryParamToken_ShouldReturnSecurityContext() {
        // Arrange
        String token = "valid-jwt-token";
        MockServerHttpRequest request = MockServerHttpRequest.get("/?token=" + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        Authentication authentication = new UsernamePasswordAuthenticationToken("user", null);
        SecurityContext expectedContext = new SecurityContextImpl();
        expectedContext.setAuthentication(authentication);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(authentication));

        // Act & Assert
        StepVerifier.create(securityContextRepository.load(exchange))
                .expectNextMatches(context ->
                        context.getAuthentication() != null &&
                                context.getAuthentication().equals(authentication))
                .verifyComplete();

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void load_WithInvalidBearerFormat_ShouldReturnEmpty() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, "InvalidFormat token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act & Assert
        StepVerifier.create(securityContextRepository.load(exchange))
                .verifyComplete();

        verifyNoInteractions(authenticationManager);
    }

    @Test
    void load_WithEmptyTokenParam_ShouldReturnEmpty() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/?token=")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act & Assert
        StepVerifier.create(securityContextRepository.load(exchange))
                .verifyComplete();

        verifyNoInteractions(authenticationManager);
    }

    @Test
    void load_WithoutToken_ShouldReturnEmpty() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act & Assert
        StepVerifier.create(securityContextRepository.load(exchange))
                .verifyComplete();

        verifyNoInteractions(authenticationManager);
    }

    @Test
    void load_WithBlankTokenParam_ShouldReturnEmpty() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest.get("/?token=   ")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act & Assert
        StepVerifier.create(securityContextRepository.load(exchange))
                .verifyComplete();

        verifyNoInteractions(authenticationManager);
    }

}