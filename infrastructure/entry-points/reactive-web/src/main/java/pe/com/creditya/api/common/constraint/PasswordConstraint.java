package pe.com.creditya.api.common.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pe.com.creditya.api.common.validators.PasswordConstraintValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordConstraint {
    String message() default "La contraseña debe tener minimo 8 caracteres, al menos un número, una mayúscula, una minúscula, un carácter especial (!@#$%^&*-) y sin espacios";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payloads() default {};
}
