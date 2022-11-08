package com.github.agrimint.service.mapper;

import com.github.agrimint.domain.*;
import com.github.agrimint.service.dto.AppUserDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link AppUser} and its DTO {@link AppUserDTO}.
 */
@Mapper(componentModel = "spring", uses = { RoleMapper.class })
public interface AppUserMapper extends EntityMapper<AppUserDTO, AppUser> {
    @Mapping(target = "authorities", source = "authorities", qualifiedByName = "nameSet")
    AppUserDTO toDto(AppUser s);

    @Mapping(target = "removeAuthorities", ignore = true)
    AppUser toEntity(AppUserDTO appUserDTO);
}
