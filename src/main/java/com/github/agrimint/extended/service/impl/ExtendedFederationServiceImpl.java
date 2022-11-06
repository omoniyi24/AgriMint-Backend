package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.CreateFederationRequestDTO;
import com.github.agrimint.extended.service.ExtendedFederationService;
import com.github.agrimint.service.FederationQueryService;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.criteria.FederationCriteria;
import com.github.agrimint.service.dto.FederationDTO;
import java.time.Instant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author OMONIYI ILESANMI
 */
@Service
public class ExtendedFederationServiceImpl implements ExtendedFederationService {

    private final FederationService federationService;
    private final FederationQueryService federationQueryService;

    public ExtendedFederationServiceImpl(FederationService federationService, FederationQueryService federationQueryService) {
        this.federationService = federationService;
        this.federationQueryService = federationQueryService;
    }

    @Override
    public FederationDTO create(CreateFederationRequestDTO createFederationRequestDTO) {
        FederationDTO federationDTO = new FederationDTO();
        federationDTO.setAlias(StringUtils.defaultIfEmpty(createFederationRequestDTO.getAlias(), createFederationRequestDTO.getName()));
        federationDTO.setName(createFederationRequestDTO.getName());
        federationDTO.setActive(true);
        federationDTO.setDateCreated(Instant.now());
        return federationService.save(federationDTO);
    }

    @Override
    public Page<FederationDTO> getAll(FederationCriteria criteria, Pageable pageable) {
        return federationQueryService.findByCriteria(criteria, pageable);
    }
}
