package com.github.agrimint.service.mapper;

import com.github.agrimint.domain.*;
import com.github.agrimint.service.dto.OtpRequestDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OtpRequest} and its DTO {@link OtpRequestDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface OtpRequestMapper extends EntityMapper<OtpRequestDTO, OtpRequest> {}
