package com.github.agrimint.service.mapper;

import com.github.agrimint.domain.*;
import com.github.agrimint.service.dto.GuardianDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Guardian} and its DTO {@link GuardianDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface GuardianMapper extends EntityMapper<GuardianDTO, Guardian> {}
