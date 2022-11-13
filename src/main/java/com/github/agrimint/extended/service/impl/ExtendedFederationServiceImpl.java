package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.CreateFederationRequestDTO;
import com.github.agrimint.extended.dto.CreateFedimintHttpRequest;
import com.github.agrimint.extended.dto.CreateFedimintHttpResponse;
import com.github.agrimint.extended.dto.GetConnectionFedimintHttpResponse;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.extended.service.ExtendedFederationService;
import com.github.agrimint.extended.service.FedimintHttpService;
import com.github.agrimint.extended.util.FedimintUtil;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.security.SecurityUtils;
import com.github.agrimint.service.FederationQueryService;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.criteria.FederationCriteria;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.FederationDTO;
import java.time.Instant;
import java.util.Optional;
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
    private final ExtendedAppUserService extendedAppUserService;

    public ExtendedFederationServiceImpl(
        FederationService federationService,
        FederationQueryService federationQueryService,
        FedimintUtil fedimintUtil,
        FedimintHttpService fedimintHttpService,
        QueryUtil queryUtil,
        ExtendedAppUserService extendedAppUserService
    ) {
        this.federationService = federationService;
        this.federationQueryService = federationQueryService;
        this.fedimintUtil = fedimintUtil;
        this.fedimintHttpService = fedimintHttpService;
        this.queryUtil = queryUtil;
        this.extendedAppUserService = extendedAppUserService;
    }

    @Override
    public FederationDTO create(CreateFederationRequestDTO createFederationRequestDTO) throws FederationExecption {
        String login = SecurityUtils.getCurrentUserLogin().get();
        Optional<AppUserDTO> userByPhoneNumberAndCountryCode = extendedAppUserService.findUserByLogin(login);
        if (userByPhoneNumberAndCountryCode.isEmpty()) {
            throw new UserException("User not Found");
        }
        createFederationRequestDTO.setName(createFederationRequestDTO.getName().toUpperCase());
        if (queryUtil.getFederationCount(createFederationRequestDTO.getName()) > 0) {
            throw new FederationExecption("Federation Already Exist");
        }

        FederationDTO federationDTO = new FederationDTO();
        federationDTO.setCreatedBy(userByPhoneNumberAndCountryCode.get().getId());
        federationDTO.setNumberOfNode(createFederationRequestDTO.getNumberOfNode());
        federationDTO.setNumberOfRegisteredNode(0);
        federationDTO.setName(createFederationRequestDTO.getName());
        federationDTO.setActive(false);
        federationDTO.setDateCreated(Instant.now());
        return federationService.save(federationDTO);
    }

    @Override
    public Page<FederationDTO> getAll(FederationCriteria criteria, Pageable pageable) {
        return federationQueryService.findByCriteria(criteria, pageable);
    }

    @Override
    public GetConnectionFedimintHttpResponse getFederationConnection(Long id) throws FederationExecption {
        Optional<FederationDTO> federationDTO = federationService.findOne(id);
        if (federationDTO.isPresent()) {
            return fedimintHttpService.getFederationConnectionString(federationDTO.get().getFedimintId());
        }
        throw new FederationExecption("Federation does not Exist");
    }
}
