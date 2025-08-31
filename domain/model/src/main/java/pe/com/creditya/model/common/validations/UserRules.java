package pe.com.creditya.model.common.validations;

import pe.com.creditya.model.common.constants.ValidConstants;
import pe.com.creditya.model.common.exceptions.BusinessValidationException;

import java.math.BigDecimal;

public class UserRules {
    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessValidationException("name", "no debe estar vacío");
        }
    }

    public static void validateLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new BusinessValidationException("lastName", "no debe estar vacío");
        }
    }

    public static void validateSalary(BigDecimal salary) {
        if (salary == null ||
                salary.compareTo(ValidConstants.MIN_SALARY) < 0 ||
                salary.compareTo(ValidConstants.MAX_SALARY) > 0) {
            throw new BusinessValidationException("baseSalary", "debe estar entre 1025 y 10000");
        }
    }

    public static void validateEmailFormat(String email) {
        if (email == null || !email.contains("@")) {
            throw new BusinessValidationException("email", "formato inválido");
        }
    }
}
