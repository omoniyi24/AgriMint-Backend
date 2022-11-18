package com.github.agrimint.extended.service.impl;

import com.github.agrimint.domain.Guardian;
import com.github.agrimint.domain.Member;
import com.github.agrimint.extended.dto.*;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.MemberAlreadyExistExecption;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.repository.ExtendedMemberRepository;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.extended.service.ExtendedGuardianService;
import com.github.agrimint.extended.service.ExtendedMemberService;
import com.github.agrimint.extended.service.FedimintHttpService;
import com.github.agrimint.extended.util.FedimintUtil;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.repository.GuardianRepository;
import com.github.agrimint.repository.MemberRepository;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.GuardianService;
import com.github.agrimint.service.MemberService;
import com.github.agrimint.service.dto.FederationDTO;
import com.github.agrimint.service.dto.GuardianDTO;
import com.github.agrimint.service.dto.MemberDTO;
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
    private final MemberRepository memberRepository;
    private final GuardianRepository guardianRepository;
    private final ExtendedMemberService extendedMemberService;

    public ExtendedGuadianServiceImpl(
        MemberService memberService,
        GuardianService guardianService,
        FederationService federationService,
        FedimintHttpService fedimintHttpService,
        QueryUtil queryUtil,
        FedimintUtil fedimintUtil,
        ExtendedAppUserService extendedAppUserService,
        ExtendedMemberRepository extendedMemberRepository,
        MemberRepository memberRepository,
        GuardianRepository guardianRepository,
        ExtendedMemberService extendedMemberService
    ) {
        this.memberService = memberService;
        this.guardianService = guardianService;
        this.federationService = federationService;
        this.fedimintHttpService = fedimintHttpService;
        this.queryUtil = queryUtil;
        this.fedimintUtil = fedimintUtil;
        this.extendedAppUserService = extendedAppUserService;
        this.extendedMemberRepository = extendedMemberRepository;
        this.memberRepository = memberRepository;
        this.guardianRepository = guardianRepository;
        this.extendedMemberService = extendedMemberService;
    }

    @Override
    public MemberDTO create(CreatMemberRequestDTO creatGuardianRequestDTO, boolean active, boolean guardian)
        throws MemberAlreadyExistExecption, FederationExecption, UserException {
        MemberDTO memberDTO = extendedMemberService.create(creatGuardianRequestDTO, false, true, false);
        Optional<FederationDTO> guardianFed = federationService.findOne(creatGuardianRequestDTO.getFederationId());
        FederationDTO federationDTO = guardianFed.get();
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
    }

    private void updateGuardian(FederationDTO federationDTO) {
        List<ExtendedGuardianDTO> byFedimintIdAndGuardianAndActive = extendedMemberRepository.findByFederationIdAndGuardianAndActive(
            federationDTO.getId(),
            true,
            false
        );
        List<Member> updatedGuardianMembers = new ArrayList<>();
        List<Guardian> updatedGuardians = new ArrayList<>();
        if (byFedimintIdAndGuardianAndActive.size() == federationDTO.getNumberOfNode()) {
            for (int nodeNumber = 0; nodeNumber < byFedimintIdAndGuardianAndActive.size(); nodeNumber++) {
                ExtendedGuardianDTO eachGuardian = byFedimintIdAndGuardianAndActive.get(nodeNumber);
                int newNodeNumber = nodeNumber + 1;
                CreateGuardianFedimintHttpResponse guardian = createGuardian(federationDTO, eachGuardian, newNodeNumber);
                Member member = new Member();
                BeanUtils.copyProperties(eachGuardian, member);
                member.setId(eachGuardian.getId());
                member.setFedimintId(guardian.get_id());
                member.setFederationId(federationDTO.getId());
                member.setActive(true);
                updatedGuardianMembers.add(member);
                Guardian guardianDTO = new Guardian();
                guardianDTO.setId(eachGuardian.getGuardianId());
                guardianDTO.setMemberId(eachGuardian.getGuardianMemberId());
                guardianDTO.setSecret(eachGuardian.getSecret());
                guardianDTO.setNodeNumber(newNodeNumber);
                guardianDTO.setInvitationSent(true);
                guardianDTO.setInvitationAccepted(true);
                updatedGuardians.add(guardianDTO);
            }

            List<Member> members = memberRepository.saveAll(updatedGuardianMembers);
            log.info("[+} ===> members {} ", members);
            List<Guardian> guardians = guardianRepository.saveAll(updatedGuardians);
            log.info("[+} ===> guardians {} ", guardians);

            List<ExtendedGuardianDTO> byFedimintIdAndGuardianAndActiveLatest = extendedMemberRepository.findByFederationIdAndGuardianAndActive(
                federationDTO.getId(),
                true,
                true
            );

            //Todo call key exchange for each guardian
            for (int i = 0; i < byFedimintIdAndGuardianAndActiveLatest.size(); i++) {
                ExtendedGuardianDTO eachGuardian = byFedimintIdAndGuardianAndActiveLatest.get(i);
                log.info("[+} ===> {} ", eachGuardian);
                JoinFedimintHttpRequest joinFedimintHttpRequest = new JoinFedimintHttpRequest();
                joinFedimintHttpRequest.setFederationId(eachGuardian.getFedimintFederationCode());
                joinFedimintHttpRequest.setSecret(String.valueOf(eachGuardian.getSecret()));
                joinFedimintHttpRequest.setNode(eachGuardian.getNodeNumber());
                fedimintHttpService.exchangeKeys(joinFedimintHttpRequest, eachGuardian.getFedimintId());
                log.info(
                    "[+] User: {} with node number: {} exchanged key for federation:  {} successfully...",
                    eachGuardian.getUserId(),
                    eachGuardian.getNodeNumber(),
                    eachGuardian.getFederationId()
                );
            }

            //Todo join each guardian to federation
            for (int i = 0; i < byFedimintIdAndGuardianAndActiveLatest.size(); i++) {
                ExtendedGuardianDTO eachGuardian = byFedimintIdAndGuardianAndActiveLatest.get(i);
                JoinFedimintHttpRequest joinFedimintHttpRequest = new JoinFedimintHttpRequest();
                joinFedimintHttpRequest.setFederationId(eachGuardian.getFedimintFederationCode());
                joinFedimintHttpRequest.setSecret(String.valueOf(eachGuardian.getSecret()));
                joinFedimintHttpRequest.setNode(eachGuardian.getNodeNumber());
                fedimintHttpService.joinFederation(joinFedimintHttpRequest, eachGuardian.getFedimintId());
                log.info(
                    "[+] User: {} with node number: {} joined federation: {} successfully...",
                    eachGuardian.getUserId(),
                    eachGuardian.getNodeNumber(),
                    eachGuardian.getFederationId()
                );
            }
        }
    }

    private CreateFedimintHttpResponse createFedimint(FederationDTO federationDTO) {
        CreateFedimintHttpRequest createFedimintHttpRequest = fedimintUtil.convertToFedimintRequest(federationDTO);
        return fedimintHttpService.createFedimint(createFedimintHttpRequest);
    }

    private CreateGuardianFedimintHttpResponse createGuardian(
        FederationDTO federationDTO,
        ExtendedGuardianDTO extendedGuardianDTO,
        Integer nodeNumber
    ) {
        CreateGuardianFedimintHttpRequest createGuardianFedimintHttpRequest = new CreateGuardianFedimintHttpRequest();
        createGuardianFedimintHttpRequest.setFederationId(federationDTO.getFedimintId());
        createGuardianFedimintHttpRequest.setSecret(String.valueOf(extendedGuardianDTO.getSecret()));
        createGuardianFedimintHttpRequest.setNode(nodeNumber);
        createGuardianFedimintHttpRequest.setName(extendedGuardianDTO.getName());
        return fedimintHttpService.createGuardian(createGuardianFedimintHttpRequest);
    }
}
