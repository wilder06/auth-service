package pe.com.creditya.api.dtos;

import jakarta.validation.constraints.Email;
import pe.com.creditya.api.common.constraint.PasswordConstraint;

public record LoginRequest(@Email String email, @PasswordConstraint String password) {
}

