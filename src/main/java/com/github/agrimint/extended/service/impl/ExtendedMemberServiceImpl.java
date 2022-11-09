package com.github.agrimint.extended.service.impl;

import static com.github.agrimint.extended.util.ApplicationConstants.FEDERATION_IS_NOT_ACTIVE;
import static com.github.agrimint.extended.util.ApplicationConstants.FEDERATION_WITH_ID_DOES_NOT_EXIST;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.MemberAlreadyExistExecption;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.extended.service.ExtendedMemberService;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.MemberQueryService;
import com.github.agrimint.service.MemberService;
import com.github.agrimint.service.criteria.MemberCriteria;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.FederationDTO;
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
    private final ExtendedAppUserService extendedAppUserService;

    public ExtendedMemberServiceImpl(
        MemberService memberService,
        MemberQueryService memberQueryService,
        FederationService federationService,
        QueryUtil queryUtil,
        ExtendedAppUserService extendedAppUserService
    ) {
        this.memberService = memberService;
        this.memberQueryService = memberQueryService;
        this.federationService = federationService;
        this.queryUtil = queryUtil;
        this.extendedAppUserService = extendedAppUserService;
    }

    @Override
    public MemberDTO create(CreatMemberRequestDTO creatMemberRequestDTO)
        throws MemberAlreadyExistExecption, FederationExecption, UserException {
        Optional<AppUserDTO> userByPhoneNumberAndCountryCode = extendedAppUserService.findUserByPhoneNumberAndCountryCode(
            creatMemberRequestDTO.getCountryCode(),
            creatMemberRequestDTO.getPhoneNumber()
        );
        if (userByPhoneNumberAndCountryCode.isPresent()) {
            Optional<FederationDTO> federation = federationService.findOne(creatMemberRequestDTO.getFederationId());
            if (federation.isEmpty()) {
                throw new FederationExecption(String.format(FEDERATION_WITH_ID_DOES_NOT_EXIST, creatMemberRequestDTO.getFederationId()));
            }
            if (federation.get().getActive().equals(Boolean.FALSE)) {
                throw new FederationExecption(String.format(FEDERATION_IS_NOT_ACTIVE, creatMemberRequestDTO.getFederationId()));
            }
            MemberDTO memberDTO = new MemberDTO();
            BeanUtils.copyProperties(creatMemberRequestDTO, memberDTO);
            memberDTO.setActive(true);
            memberDTO.setGuardian(false);
            memberDTO.setDateCreated(Instant.now());
            memberDTO.setUserId(userByPhoneNumberAndCountryCode.get().getId());
            MemberDTO savedMember = memberService.save(memberDTO);
            queryUtil.persistFederationMember(creatMemberRequestDTO.getFederationId(), savedMember.getId());
            return savedMember;
        } else {
            throw new UserException("User not found");
        }
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
