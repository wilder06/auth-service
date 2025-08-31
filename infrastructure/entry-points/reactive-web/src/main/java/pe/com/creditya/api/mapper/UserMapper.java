package pe.com.creditya.api.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pe.com.creditya.api.dtos.UserRequest;
import pe.com.creditya.api.dtos.UserResponse;
import pe.com.creditya.model.user.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toUser(UserRequest request);

    UserResponse toUserResponse(User user);
}