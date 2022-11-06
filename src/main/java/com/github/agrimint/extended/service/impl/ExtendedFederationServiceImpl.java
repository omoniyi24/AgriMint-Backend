package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.CreateFederationRequestDTO;
import com.github.agrimint.extended.dto.CreateFedimintHttpRequest;
import com.github.agrimint.extended.dto.CreateFedimintHttpResponse;
import com.github.agrimint.extended.exeception.FederationExecption;
import com.github.agrimint.extended.exeception.MemberAlreadyExistExecption;
import com.github.agrimint.extended.service.ExtendedFederationService;
import com.github.agrimint.extended.service.FedimintHttpService;
import com.github.agrimint.extended.util.FedimintUtil;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.service.FederationQueryService;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.criteria.FederationCriteria;
import com.github.agrimint.service.dto.FederationDTO;
import java.time.Instant;
import java.util.Locale;
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
    private final FedimintUtil fedimintUtil;
    private final FedimintHttpService fedimintHttpService;
    private final QueryUtil queryUtil;

    public ExtendedFederationServiceImpl(
        FederationService federationService,
        FederationQueryService federationQueryService,
        FedimintUtil fedimintUtil,
        FedimintHttpService fedimintHttpService,
        QueryUtil queryUtil
    ) {
        this.federationService = federationService;
        this.federationQueryService = federationQueryService;
        this.fedimintUtil = fedimintUtil;
        this.fedimintHttpService = fedimintHttpService;
        this.queryUtil = queryUtil;
    }

    @Override
    public FederationDTO create(CreateFederationRequestDTO createFederationRequestDTO) throws FederationExecption {
        createFederationRequestDTO.setName(createFederationRequestDTO.getName().toLowerCase());
        if (queryUtil.getFederationCount(createFederationRequestDTO.getName()) > 0) {
            throw new FederationExecption("Federation Already Exist");
        }
        CreateFedimintHttpRequest createFedimintHttpRequest = fedimintUtil.convertToFedimintRequest(createFederationRequestDTO);
        CreateFedimintHttpResponse federation = fedimintHttpService.createFedimint(createFedimintHttpRequest);
        FederationDTO federationDTO = new FederationDTO();
        federationDTO.setNumberOfNode(createFederationRequestDTO.getNumberOfNode());
        federationDTO.setNumberOfRegisteredNode(0);
        federationDTO.setName(createFederationRequestDTO.getName());
        federationDTO.setActive(false);
        federationDTO.setDateCreated(Instant.now());
        federationDTO.setFedimintId(federation.get_id());
        federationDTO.setBasePort(federation.getBasePort());
        return federationService.save(federationDTO);
    }

    @Override
    public Page<FederationDTO> getAll(FederationCriteria criteria, Pageable pageable) {
        return federationQueryService.findByCriteria(criteria, pageable);
    }
}
