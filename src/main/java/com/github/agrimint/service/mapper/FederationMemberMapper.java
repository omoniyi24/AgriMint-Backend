package com.github.agrimint.service.mapper;

import com.github.agrimint.domain.*;
import com.github.agrimint.service.dto.FederationMemberDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FederationMember} and its DTO {@link FederationMemberDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface FederationMemberMapper extends EntityMapper<FederationMemberDTO, FederationMember> {}
