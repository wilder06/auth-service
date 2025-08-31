package pe.com.creditya.model.common.validations;

import pe.com.creditya.model.user.User;
public class UserValidator {
    public void validate(User user) {
        UserRules.validateName(user.getName());
        UserRules.validateLastName(user.getLastName());
        UserRules.validateSalary(user.getBaseSalary());
        UserRules.validateEmailFormat(user.getEmail());
    }
}
