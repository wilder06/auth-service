package pe.com.creditya.api.dtos;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
public record UserRequest(
        @NotBlank String name,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
        String phoneNumber,
        @NotBlank
        String documentNumber,
        String password,
        @NotNull Long idRole,
        String address,
        @NotNull @DecimalMin("0.00") @DecimalMax("15000000") BigDecimal baseSalary) {
}
