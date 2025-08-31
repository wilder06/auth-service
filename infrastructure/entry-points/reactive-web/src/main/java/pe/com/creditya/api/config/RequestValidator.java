package pe.com.creditya.api.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pe.com.creditya.api.constants.UserConstants;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    public <T> Mono<T> validate(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            log.warn(UserConstants.LOGGER_VALIDATION_FAIL,
                    request,
                    violations.stream().map(ConstraintViolation::getMessage).toList());
            return Mono.error(new ConstraintViolationException(violations));
        }
        log.info(UserConstants.LOGGER_VALIDATION_SUCCESS, request);
        return Mono.just(request);
    }

}