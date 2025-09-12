package pe.com.creditya.model.user;
import lombok.*;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private String id;
    private String name;
    private String lastName;
    private LocalDate birthDate;
    private String address;
    private String phoneNumber;
    private String documentNumber;
    private String email;
    private String password;
    private BigDecimal baseSalary;
    private Long idRole;
}