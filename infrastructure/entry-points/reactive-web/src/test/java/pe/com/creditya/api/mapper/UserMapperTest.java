package pe.com.creditya.api.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pe.com.creditya.api.dtos.UserRequest;
import pe.com.creditya.api.dtos.UserResponse;
import pe.com.creditya.model.role.RoleEnum;
import pe.com.creditya.model.user.User;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapUserRequestToUser() {
        UserRequest request = UserRequest.builder()
                .name("John")
                .lastName("demo")
                .email("john.demo@test.com")
                .phoneNumber("51987456321")
                .documentNumber("32178987")
                .address("Lima, Peru")
                .role(RoleEnum.USER.name())
                .baseSalary(BigDecimal.valueOf(4000.0)).build();

        User user = mapper.toUser(request);

        assertThat(user).isNotNull();
    }

    @Test
    void shouldMapUserToUserResponse() {
        User user = new User();
        user.setId(String.valueOf(1L));
        user.setName("Jane");
        user.setLastName("perez");
        user.setEmail("jane.perez@test.com");
        user.setPhoneNumber("51987654321");
        user.setDocumentNumber("12452356");
        user.setAddress("Arequipa, Peru");
        user.setIdRole(2L);
        user.setBaseSalary(BigDecimal.valueOf(4000.0));

        UserResponse response = mapper.toUserResponse(user);
        assertThat(response).isNotNull();
    }
}