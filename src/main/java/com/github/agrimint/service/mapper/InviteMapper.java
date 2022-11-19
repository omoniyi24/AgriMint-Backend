package com.github.agrimint.service.mapper;

import com.github.agrimint.domain.*;
import com.github.agrimint.service.dto.InviteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Invite} and its DTO {@link InviteDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface InviteMapper extends EntityMapper<InviteDTO, Invite> {}
