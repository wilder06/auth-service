package pe.com.creditya.api.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "El apellido no puede estar vacío")
    private String lastName;

    private LocalDate birthDate;

    private String address;

    private String phoneNumber;

    @NotBlank(message = "El correo electrónico no puede estar vacío")
    @Email(message = "El correo electrónico no tiene un formato válido")
    private String email;

    @NotNull(message = "El salario base no puede ser nulo")
    @Min(value = 0, message = "El salario base no puede ser menor que 0")
    @Max(value = 15000000, message = "El salario base no puede superar 15,000,000")
    private BigDecimal baseSalary;
}