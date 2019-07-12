package ar.org.example.service.mapper;


import ar.org.example.domain.Role;
import ar.org.example.service.dto.RoleDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity Role and its DTO RoleDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface RoleMapper extends EntityMapper<RoleDTO, Role> {

    default Role fromId(Long id) {
        if (id == null) {
            return null;
        }
        Role role = new Role();
        role.setId(id);
        return role;
    }
}
