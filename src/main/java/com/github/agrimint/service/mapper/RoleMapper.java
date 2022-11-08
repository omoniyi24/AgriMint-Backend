package com.github.agrimint.service.mapper;

import com.github.agrimint.domain.*;
import com.github.agrimint.service.dto.RoleDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Role} and its DTO {@link RoleDTO}.
 */
@Mapper(componentModel = "spring", uses = { PrivilegeMapper.class })
public interface RoleMapper extends EntityMapper<RoleDTO, Role> {
    @Mapping(target = "authorities", source = "authorities", qualifiedByName = "nameSet")
    RoleDTO toDto(Role s);

    @Mapping(target = "removeAuthorities", ignore = true)
    Role toEntity(RoleDTO roleDTO);

    @Named("nameSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Set<RoleDTO> toDtoNameSet(Set<Role> role);
}
