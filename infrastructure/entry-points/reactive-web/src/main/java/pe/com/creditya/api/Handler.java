package pe.com.creditya.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pe.com.creditya.api.common.ValidatorConfig;
import pe.com.creditya.api.common.constants.UserConstants;
import pe.com.creditya.api.dtos.UserRequest;
import pe.com.creditya.api.mapper.UserMapper;
import pe.com.creditya.usecase.user.IUserUseCase;
import pe.com.creditya.usecase.user.UserUseCase;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
    private final IUserUseCase iUserUseCase;
    private final ValidatorConfig requestValidator;
    private final UserMapper userMapper;


    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequest.class).doOnNext(req ->
                        log.info(UserConstants.LOGGER_START, req))
                .flatMap(requestValidator::validate)
                .map(userMapper::toUser)
                .doOnNext(user -> log.debug(UserConstants.LOGGER_MAP, user))
                .flatMap(iUserUseCase::saveUser)
                .doOnNext(user -> log.info(UserConstants.LOGGER_SAVE_SUCCESS, user.getEmail()))
                .map(userMapper::toUserResponse)
                .flatMap(savedUser -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser))
                .doOnError(ex -> log.error(UserConstants.LOGGER_SAVE_FAIL, ex.getMessage(), ex));

    }
    public Mono<ServerResponse> listenGetTaskByDocumentNumber(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("documentNumber");
        return iUserUseCase.findByDocumentNumber(id)
                .map(userMapper::toUserResponse)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}