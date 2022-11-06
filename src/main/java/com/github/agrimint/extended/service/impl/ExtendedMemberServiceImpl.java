package com.github.agrimint.extended.service.impl;

import static com.github.agrimint.extended.util.ApplicationConstants.FEDERATION_WITH_ID_DOES_NOT_EXIST;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
import com.github.agrimint.extended.exeception.FederationExecption;
import com.github.agrimint.extended.exeception.MemberAlreadyExistExecption;
import com.github.agrimint.extended.service.ExtendedMemberService;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.MemberQueryService;
import com.github.agrimint.service.MemberService;
import com.github.agrimint.service.criteria.MemberCriteria;
import com.github.agrimint.service.dto.MemberDTO;
import java.time.Instant;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author OMONIYI ILESANMI
 */
@Service
public class ExtendedMemberServiceImpl implements ExtendedMemberService {

    private final MemberService memberService;
    private final MemberQueryService memberQueryService;
    private final FederationService federationService;
    private final QueryUtil queryUtil;

    public ExtendedMemberServiceImpl(
        MemberService memberService,
        MemberQueryService memberQueryService,
        FederationService federationService,
        QueryUtil queryUtil
    ) {
        this.memberService = memberService;
        this.memberQueryService = memberQueryService;
        this.federationService = federationService;
        this.queryUtil = queryUtil;
    }

    @Override
    public MemberDTO create(CreatMemberRequestDTO creatMemberRequestDTO) throws MemberAlreadyExistExecption, FederationExecption {
        if (federationService.findOne(creatMemberRequestDTO.getFederationId()).isEmpty()) {
            throw new FederationExecption(String.format(FEDERATION_WITH_ID_DOES_NOT_EXIST, creatMemberRequestDTO.getFederationId()));
        }
        MemberDTO memberDTO = new MemberDTO();
        BeanUtils.copyProperties(creatMemberRequestDTO, memberDTO);
        memberDTO.setActive(true);
        memberDTO.setGuardian(false);
        memberDTO.setDateCreated(Instant.now());
        MemberDTO savedMember = memberService.save(memberDTO);
        queryUtil.persistFederationMember(creatMemberRequestDTO.getFederationId(), savedMember.getId());
        return savedMember;
    }

    @Override
    public Page<MemberDTO> getAll(MemberCriteria criteria, Pageable pageable) {
        return memberQueryService.findByCriteria(criteria, pageable);
    }

    @Override
    public Optional<MemberDTO> getOne(Long id) {
        return memberService.findOne(id);
    }
}
