package pe.com.creditya.usecase.auth;

import lombok.RequiredArgsConstructor;
import pe.com.creditya.model.common.constants.LogConstants;
import pe.com.creditya.model.common.exceptions.InvalidCredentialsException;
import pe.com.creditya.model.common.exceptions.TechnicalException;
import pe.com.creditya.model.token.gateways.TokenRepository;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthUseCase implements IAuthUseCase {
    private final TokenRepository authTokenRepository;

    @Override
    public Mono<String> authenticate(String email, String password) {
        return authTokenRepository.generateToken(email, password)
                .onErrorMap(InvalidCredentialsException.class, ex ->
                        new InvalidCredentialsException(LogConstants.LOGGER_ERROR_AUTHENTICATE, ex)
                );
    }
}
