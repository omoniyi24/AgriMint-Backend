package com.github.agrimint.service.mapper;

import com.github.agrimint.domain.*;
import com.github.agrimint.service.dto.PrivilegeDTO;
import java.util.Set;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Privilege} and its DTO {@link PrivilegeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PrivilegeMapper extends EntityMapper<PrivilegeDTO, Privilege> {
    @Named("nameSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Set<PrivilegeDTO> toDtoNameSet(Set<Privilege> privilege);
}
