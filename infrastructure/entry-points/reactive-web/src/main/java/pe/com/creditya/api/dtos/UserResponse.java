package pe.com.creditya.api.dtos;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
@Builder
public record UserResponse(
        String name,
        String lastName,
        LocalDate birthDate,
        String address,
        String phoneNumber,
        String documentNumber,
        String email,
        String role,
        BigDecimal baseSalary
) {
}