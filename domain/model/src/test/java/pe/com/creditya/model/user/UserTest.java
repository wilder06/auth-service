package pe.com.creditya.model.user;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void shouldBuildUserWithBuilder() {
        User user = User.builder()
                .id("1")
                .name("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Street")
                .phoneNumber("987654321")
                .email("john.doe@example.com")
                .baseSalary(new BigDecimal("2500.50"))
                .build();

        assertAll(
                () -> assertEquals("1", user.getId()),
                () -> assertEquals("John", user.getName()),
                () -> assertEquals("Doe", user.getLastName()),
                () -> assertEquals(LocalDate.of(1990, 1, 1), user.getBirthDate()),
                () -> assertEquals("123 Street", user.getAddress()),
                () -> assertEquals("987654321", user.getPhoneNumber()),
                () -> assertEquals("john.doe@example.com", user.getEmail()),
                () -> assertEquals(new BigDecimal("2500.50"), user.getBaseSalary())
        );
    }

}