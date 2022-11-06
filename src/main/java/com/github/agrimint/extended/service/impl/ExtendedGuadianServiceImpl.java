package com.github.agrimint.extended.service.impl;

import static com.github.agrimint.extended.util.ApplicationConstants.FEDERATION_WITH_ID_DOES_NOT_EXIST;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
import com.github.agrimint.extended.exeception.FederationExecption;
import com.github.agrimint.extended.exeception.MemberAlreadyExistExecption;
import com.github.agrimint.extended.service.ExtendedGuardianService;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.GuardianService;
import com.github.agrimint.service.MemberService;
import com.github.agrimint.service.dto.GuardianDTO;
import com.github.agrimint.service.dto.MemberDTO;
import java.time.Instant;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author OMONIYI ILESANMI
 */
@Service
public class ExtendedGuadianServiceImpl implements ExtendedGuardianService {

    private final MemberService memberService;
    private final GuardianService guardianService;
    private final FederationService federationService;
    private final QueryUtil queryUtil;

    public ExtendedGuadianServiceImpl(
        MemberService memberService,
        GuardianService guardianService,
        FederationService federationService,
        QueryUtil queryUtil
    ) {
        this.memberService = memberService;
        this.guardianService = guardianService;
        this.federationService = federationService;
        this.queryUtil = queryUtil;
    }

    @Override
    public MemberDTO create(CreatMemberRequestDTO creatGuardianRequestDTO) throws MemberAlreadyExistExecption, FederationExecption {
        if (federationService.findOne(creatGuardianRequestDTO.getFederationId()).isEmpty()) {
            throw new FederationExecption(String.format(FEDERATION_WITH_ID_DOES_NOT_EXIST, creatGuardianRequestDTO.getFederationId()));
        }
        MemberDTO memberDTO = new MemberDTO();
        BeanUtils.copyProperties(creatGuardianRequestDTO, memberDTO);
        memberDTO.setActive(true);
        memberDTO.setGuardian(true);
        memberDTO.setDateCreated(Instant.now());
        memberDTO = memberService.save(memberDTO);
        if (memberDTO.getGuardian()) {
            GuardianDTO guardianDTO = new GuardianDTO();
            guardianDTO.setMemberId(memberDTO.getId());
            guardianDTO.setInvitationAccepted(false);
            guardianDTO.setInvitationSent(false);
            guardianService.save(guardianDTO);
        }
        queryUtil.persistFederationMember(creatGuardianRequestDTO.getFederationId(), memberDTO.getId());
        return memberDTO;
    }
}
