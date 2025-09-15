package pe.com.creditya.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pe.com.creditya.api.dtos.UserRequest;
import pe.com.creditya.api.dtos.UserResponse;
import pe.com.creditya.model.role.RoleEnum;
import pe.com.creditya.model.user.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "idRole", source = "request.role", qualifiedByName = "getRoleId")
    @Mapping(target = "id", ignore = true)
    User toUser(UserRequest request);
    @Mapping(target = "role", source = "user.idRole", qualifiedByName = "getRoleName")

    UserResponse toUserResponse(User user);

    @Named("getRoleId")
    default Long getRoleId(String roleName) {
        return RoleEnum.fromName(roleName);
    }
    @Named("getRoleName")
    default String getRoleName(Long idRole) {
        return RoleEnum.fromId(idRole).name();
    }
}