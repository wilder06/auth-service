package pe.com.creditya.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pe.com.creditya.api.common.validators.ValidatorConfig;
import pe.com.creditya.api.common.constants.UserConstants;
import pe.com.creditya.api.dtos.UserRequest;
import pe.com.creditya.api.mapper.UserMapper;
import pe.com.creditya.usecase.user.IUserUseCase;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
    private final IUserUseCase iUserUseCase;
    private final ValidatorConfig requestValidator;
    private final UserMapper userMapper;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
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

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public Mono<ServerResponse> listenGetUserByDocumentNumber(ServerRequest serverRequest) {
        String documentNumber = serverRequest.pathVariable(UserConstants.VARIABLE_NAME);
        return iUserUseCase.findByDocumentNumber(documentNumber)
                .map(userMapper::toUserResponse)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADVISOR')")
    public Mono<ServerResponse> getUsersByEmails(ServerRequest request) {
        return request.bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Request body is required")))
                .flatMap(requestValidator::validate)
                .flatMapMany(iUserUseCase::findByEmails)
                .map(userMapper::toUserResponse)
                .collectList()
                .flatMap(users -> users.isEmpty() ?
                        ServerResponse.notFound().build() :
                        ServerResponse.ok().bodyValue(users)
                )
                .onErrorResume(IllegalArgumentException.class, ex ->
                        ServerResponse.badRequest().bodyValue(Map.of("error", ex.getMessage()))
                );
    }
}