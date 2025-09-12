package pe.com.creditya.api.common.validators;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import pe.com.creditya.api.common.constants.UserConstants;
import pe.com.creditya.model.common.validations.UserValidator;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidatorConfig {
    private final Validator validator;

    @Bean
    public UserValidator userValidator() {
        return new UserValidator();
    }

    public <T> Mono<T> validate(T request) {
        return Mono.just(request)
                .handle((validRequest, sink) -> {
                    Set<ConstraintViolation<T>> violations = validator.validate(validRequest);
                    if (violations.isEmpty()) {
                        sink.next(validRequest);
                    } else {
                        sink.error(new ConstraintViolationException(violations));
                    }
                });
    }
}
