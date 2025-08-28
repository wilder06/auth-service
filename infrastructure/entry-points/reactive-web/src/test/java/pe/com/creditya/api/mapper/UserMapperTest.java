package pe.com.creditya.api.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pe.com.creditya.api.dtos.UserRequest;
import pe.com.creditya.api.dtos.UserResponse;
import pe.com.creditya.model.user.User;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapUserRequestToUser() {
        UserRequest request = new UserRequest();
        request.setName("John");
        request.setLastName("demo");
        request.setEmail("john.demo@test.com");
        request.setPhoneNumber("51987456321");
        request.setAddress("Lima, Peru");
        request.setBaseSalary(BigDecimal.valueOf(4000.0));

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
        user.setAddress("Arequipa, Peru");
        user.setBaseSalary(BigDecimal.valueOf(4000.0));

        UserResponse response = mapper.toUserResponse(user);
        assertThat(response).isNotNull();
    }
}