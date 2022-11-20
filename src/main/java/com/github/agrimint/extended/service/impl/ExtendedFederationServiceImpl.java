package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.CreateFederationRequestDTO;
import com.github.agrimint.extended.dto.GetConnectionFedimintHttpResponse;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.extended.service.ExtendedFederationService;
import com.github.agrimint.extended.service.FedimintHttpService;
import com.github.agrimint.extended.util.FedimintUtil;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.extended.util.UserUtil;
import com.github.agrimint.security.SecurityUtils;
import com.github.agrimint.service.FederationQueryService;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.criteria.FederationCriteria;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.FederationDTO;
import com.github.agrimint.service.dto.MemberDTO;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
    private final UserUtil userUtil;
    private final ExtendedAppUserService extendedAppUserService;

    public ExtendedFederationServiceImpl(
        FederationService federationService,
        FederationQueryService federationQueryService,
        FedimintUtil fedimintUtil,
        FedimintHttpService fedimintHttpService,
        QueryUtil queryUtil,
        UserUtil userUtil,
        ExtendedAppUserService extendedAppUserService
    ) {
        this.federationService = federationService;
        this.federationQueryService = federationQueryService;
        this.fedimintUtil = fedimintUtil;
        this.fedimintHttpService = fedimintHttpService;
        this.queryUtil = queryUtil;
        this.userUtil = userUtil;
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
    public List<FederationDTO> getAll(FederationCriteria criteria, Pageable pageable) {
        AppUserDTO loggedInUser = userUtil.getLoggedInUser();
        List<MemberDTO> memberByUserId = queryUtil.getMemberByUserId(loggedInUser.getId());
        List<FederationDTO> allFed = new ArrayList<>();
        memberByUserId
            .stream()
            .forEach(
                eachMember -> {
                    Optional<FederationDTO> federation = federationService.findOne(eachMember.getFederationId());
                    if (federation.isPresent()) {
                        allFed.add(federation.get());
                    }
                }
            );
        return allFed;
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
