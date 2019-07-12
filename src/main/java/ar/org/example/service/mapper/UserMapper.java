package ar.org.example.service.mapper;


import ar.org.example.domain.User;
import ar.org.example.service.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity User and its DTO UserDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface UserMapper extends EntityMapper<UserDTO, User> {

    @Mapping(source = "roles", target = "roles")
    UserDTO toDto(User user);

    @Mapping(source = "roles", target = "roles")
    User toEntity(UserDTO userDTO);

    default User fromId(Long id) {
        if (id == null) {
            return null;
        }
        User user = new User();
        user.setId(id);
        return user;
    }
}
