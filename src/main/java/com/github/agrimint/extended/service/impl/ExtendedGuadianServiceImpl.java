package com.github.agrimint.extended.service.impl;

import static com.github.agrimint.extended.util.ApplicationConstants.FEDERATION_WITH_ID_DOES_NOT_EXIST;

import com.github.agrimint.domain.Member;
import com.github.agrimint.extended.dto.*;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.MemberAlreadyExistExecption;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.repository.ExtendedMemberRepository;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.extended.service.ExtendedGuardianService;
import com.github.agrimint.extended.service.FedimintHttpService;
import com.github.agrimint.extended.util.FedimintUtil;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.GuardianService;
import com.github.agrimint.service.MemberService;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.FederationDTO;
import com.github.agrimint.service.dto.GuardianDTO;
import com.github.agrimint.service.dto.MemberDTO;
import com.github.agrimint.service.mapper.MemberMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author OMONIYI ILESANMI
 */
@Service
@Slf4j
public class ExtendedGuadianServiceImpl implements ExtendedGuardianService {

    private final MemberService memberService;
    private final GuardianService guardianService;
    private final FederationService federationService;
    private final FedimintHttpService fedimintHttpService;
    private final QueryUtil queryUtil;
    private final FedimintUtil fedimintUtil;
    private final ExtendedAppUserService extendedAppUserService;
    private final ExtendedMemberRepository extendedMemberRepository;
    private final MemberMapper memberMapper;

    public ExtendedGuadianServiceImpl(
        MemberService memberService,
        GuardianService guardianService,
        FederationService federationService,
        FedimintHttpService fedimintHttpService,
        QueryUtil queryUtil,
        FedimintUtil fedimintUtil,
        ExtendedAppUserService extendedAppUserService,
        ExtendedMemberRepository extendedMemberRepository,
        MemberMapper memberMapper
    ) {
        this.memberService = memberService;
        this.guardianService = guardianService;
        this.federationService = federationService;
        this.fedimintHttpService = fedimintHttpService;
        this.queryUtil = queryUtil;
        this.fedimintUtil = fedimintUtil;
        this.extendedAppUserService = extendedAppUserService;
        this.extendedMemberRepository = extendedMemberRepository;
        this.memberMapper = memberMapper;
    }

    @Override
    public MemberDTO create(CreatMemberRequestDTO creatGuardianRequestDTO)
        throws MemberAlreadyExistExecption, FederationExecption, UserException {
        Optional<AppUserDTO> userByPhoneNumberAndCountryCode = extendedAppUserService.findUserByPhoneNumberAndCountryCode(
            creatGuardianRequestDTO.getCountryCode(),
            creatGuardianRequestDTO.getPhoneNumber()
        );
        if (userByPhoneNumberAndCountryCode.isPresent()) {
            Optional<FederationDTO> guradianFed = federationService.findOne(creatGuardianRequestDTO.getFederationId());
            if (guradianFed.isEmpty()) {
                throw new FederationExecption(String.format(FEDERATION_WITH_ID_DOES_NOT_EXIST, creatGuardianRequestDTO.getFederationId()));
            }
            FederationDTO federationDTO = guradianFed.get();
            CreateGuardianFedimintHttpRequest createFedimintHttpRequest = fedimintUtil.convertToFedimintRequest(
                creatGuardianRequestDTO,
                federationDTO
            );
            MemberDTO memberDTO = new MemberDTO();
            BeanUtils.copyProperties(creatGuardianRequestDTO, memberDTO);
            memberDTO.setActive(false);
            memberDTO.setGuardian(true);
            memberDTO.setFedimintId(createFedimintHttpRequest.getFederationId());
            memberDTO.setDateCreated(Instant.now());
            memberDTO.setUserId(userByPhoneNumberAndCountryCode.get().getId());
            memberDTO = memberService.save(memberDTO);
            if (memberDTO.getGuardian()) {
                GuardianDTO guardianDTO = new GuardianDTO();
                guardianDTO.setMemberId(memberDTO.getId());
                guardianDTO.setInvitationAccepted(false);
                guardianDTO.setInvitationSent(false);
                guardianDTO.setSecret(creatGuardianRequestDTO.getSecret());
                guardianDTO.setNodeNumber(creatGuardianRequestDTO.getNodeNumber());
                guardianService.save(guardianDTO);
                int registeredNode = federationDTO.getNumberOfRegisteredNode();
                int newNumberOfRegisteredNode = registeredNode + 1;
                federationDTO.setNumberOfRegisteredNode(newNumberOfRegisteredNode);
                federationDTO = federationService.save(federationDTO);
            }
            queryUtil.persistFederationMember(creatGuardianRequestDTO.getFederationId(), memberDTO.getId());
            if (federationDTO.getNumberOfNode().equals(federationDTO.getNumberOfRegisteredNode())) {
                log.info("[+] Setting up Fedimint Because All Guardians are available ===>> ");
                CreateFedimintHttpResponse fedimint = createFedimint(federationDTO);
                federationDTO.setFedimintId(fedimint.get_id());
                federationDTO.setBasePort(fedimint.getBasePort());
                federationDTO.setActive(true);
                federationDTO = federationService.save(federationDTO);
                if (fedimint.get_id() != null && StringUtils.isNotBlank(fedimint.get_id())) {
                    updateGuardian(federationDTO);
                }
            }
            return memberDTO;
        } else {
            throw new UserException("User not found");
        }
    }

    private void updateGuardian(FederationDTO federationDTO) {
        List<ExtendedGuardianDTO> byFedimintIdAndGuardianAndActive = extendedMemberRepository.findByFederationIdAndGuardianAndActive(
            federationDTO.getId(),
            true,
            false
        );
        List<Member> updatedGuardians = new ArrayList<>();
        if (byFedimintIdAndGuardianAndActive.size() == federationDTO.getNumberOfNode()) {
            byFedimintIdAndGuardianAndActive.forEach(
                eachGuardian -> {
                    CreateGuardianFedimintHttpResponse guardian = createGuardian(federationDTO, eachGuardian);
                    Member member = new Member();
                    BeanUtils.copyProperties(eachGuardian, member);
                    member.setFedimintId(guardian.get_id());
                    member.setFederationId(federationDTO.getId());
                    member.setActive(true);
                    updatedGuardians.add(member);
                }
            );
        }
        extendedMemberRepository.saveAll(updatedGuardians);
    }

    private CreateFedimintHttpResponse createFedimint(FederationDTO federationDTO) {
        CreateFedimintHttpRequest createFedimintHttpRequest = fedimintUtil.convertToFedimintRequest(federationDTO);
        return fedimintHttpService.createFedimint(createFedimintHttpRequest);
    }

    private CreateGuardianFedimintHttpResponse createGuardian(FederationDTO federationDTO, ExtendedGuardianDTO extendedGuardianDTO) {
        CreateGuardianFedimintHttpRequest createGuardianFedimintHttpRequest = new CreateGuardianFedimintHttpRequest();
        createGuardianFedimintHttpRequest.setFederationId(federationDTO.getFedimintId());
        createGuardianFedimintHttpRequest.setSecret(extendedGuardianDTO.getSecret());
        createGuardianFedimintHttpRequest.setNode(extendedGuardianDTO.getNodeNumber());
        createGuardianFedimintHttpRequest.setName(extendedGuardianDTO.getName());
        return fedimintHttpService.createGuardian(createGuardianFedimintHttpRequest);
    }
}
