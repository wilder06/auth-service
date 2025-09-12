package pe.com.creditya.api.common.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pe.com.creditya.api.common.constraint.PasswordConstraint;

public class PasswordConstraintValidator implements ConstraintValidator<PasswordConstraint, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        return password.length() >= 8 &&
                password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*-])(?=\\S+$).{8,}$");
    }
}
