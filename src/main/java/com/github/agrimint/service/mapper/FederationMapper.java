package com.github.agrimint.service.mapper;

import com.github.agrimint.domain.*;
import com.github.agrimint.service.dto.FederationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Federation} and its DTO {@link FederationDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FederationMapper extends EntityMapper<FederationDTO, Federation> {}
