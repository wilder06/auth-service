package pe.com.creditya.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pe.com.creditya.api.common.constants.UserConstants;
import pe.com.creditya.api.dtos.*;
import pe.com.creditya.usecase.auth.AuthUseCase;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHandler {
    private final AuthUseCase authUseCase;

    public Mono<ServerResponse> listenLogin(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(LoginRequest.class)
                .doOnNext(u -> log.info(UserConstants.LOGGER_START_LOGIN, u))
                .flatMap(loginRequest -> authUseCase
                        .authenticate(loginRequest.email(), loginRequest.password()))
                .doOnNext(u -> log.info(UserConstants.LOGGER_END_LOGIN, u))
                .flatMap(token -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new AuthTokenResponse(token)));
    }

}

