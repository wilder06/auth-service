package pe.com.creditya.model.role;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum RoleEnum {
    ADMIN(1L, "Rol para el Administrador"),
    USER(2L, "Rol para el cliente"),
    ADVISOR(3L, "Rol para el Asesos");
    private final Long id;
    private final String description;


    public static Long fromName(String name) {
        return Arrays.stream(values())
                .filter(e -> e.name().equalsIgnoreCase(name))
                .findFirst()
                .map(RoleEnum::getId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid loan type: " + name));
    }

    public static RoleEnum fromId(Long id) {
        return Arrays.stream(values())
                .filter(e -> Objects.equals(e.id, id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid loan type id: " + id));
    }
}
