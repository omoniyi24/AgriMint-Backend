package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
import com.github.agrimint.extended.dto.CreateFedimintMemberHttpRequest;
import com.github.agrimint.extended.dto.GetConnectionFedimintHttpResponse;
import com.github.agrimint.extended.dto.MemberJoinFedimintHttpRequest;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.MemberExecption;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.extended.service.ExtendedMemberService;
import com.github.agrimint.extended.service.FedimintHttpService;
import com.github.agrimint.extended.util.FederationUtil;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.extended.util.UserUtil;
import com.github.agrimint.security.SecurityUtils;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.InviteService;
import com.github.agrimint.service.MemberQueryService;
import com.github.agrimint.service.MemberService;
import com.github.agrimint.service.criteria.MemberCriteria;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.FederationDTO;
import com.github.agrimint.service.dto.InviteDTO;
import com.github.agrimint.service.dto.MemberDTO;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.jhipster.service.filter.LongFilter;

/**
 * @author OMONIYI ILESANMI
 */
@Service
public class ExtendedMemberServiceImpl implements ExtendedMemberService {

    private final MemberService memberService;
    private final MemberQueryService memberQueryService;
    private final FederationService federationService;
    private final FederationUtil federationUtil;
    private final QueryUtil queryUtil;
    private final UserUtil userUtil;
    private final ExtendedAppUserService extendedAppUserService;
    private final InviteService inviteService;
    private final FedimintHttpService fedimintHttpService;

    public ExtendedMemberServiceImpl(
        MemberService memberService,
        MemberQueryService memberQueryService,
        FederationService federationService,
        FederationUtil federationUtil,
        QueryUtil queryUtil,
        UserUtil userUtil,
        ExtendedAppUserService extendedAppUserService,
        InviteService inviteService,
        FedimintHttpService fedimintHttpService
    ) {
        this.memberService = memberService;
        this.memberQueryService = memberQueryService;
        this.federationService = federationService;
        this.federationUtil = federationUtil;
        this.queryUtil = queryUtil;
        this.userUtil = userUtil;
        this.extendedAppUserService = extendedAppUserService;
        this.inviteService = inviteService;
        this.fedimintHttpService = fedimintHttpService;
    }

    @Override
    public MemberDTO create(
        CreatMemberRequestDTO creatMemberRequestDTO,
        boolean active,
        boolean guardian,
        boolean checkFederation,
        String invitationCode
    ) throws MemberExecption, FederationExecption, UserException {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        Optional<AppUserDTO> userByPhoneNumberAndCountryCode = extendedAppUserService.findUserByLogin(currentUserLogin.get());

        if (userByPhoneNumberAndCountryCode.isPresent()) {
            AppUserDTO appUserDTO = userByPhoneNumberAndCountryCode.get();
            if (StringUtils.isNotBlank(invitationCode)) {
                Optional<InviteDTO> savedInvitationCode = queryUtil.getInvitationCode(
                    appUserDTO.getPhoneNumber(),
                    appUserDTO.getCountryCode(),
                    invitationCode
                );
                if (savedInvitationCode.isPresent()) {
                    InviteDTO inviteDTO = savedInvitationCode.get();
                    creatMemberRequestDTO.setFederationId(inviteDTO.getFederationId());
                    inviteDTO.setActive(false);
                    inviteService.save(inviteDTO);
                } else {
                    throw new UserException("Invalid Invitation Code");
                }
            }

            FederationDTO federationDTO = federationUtil.getFederation(creatMemberRequestDTO.getFederationId(), checkFederation);

            Optional<MemberDTO> memberByUserId = queryUtil.getMemberByUserIdAndFederationId(appUserDTO.getId(), federationDTO.getId());
            if (memberByUserId.isPresent()) {
                throw new MemberExecption("Member Already Exist In System");
            }
            MemberDTO memberDTO = new MemberDTO();
            BeanUtils.copyProperties(creatMemberRequestDTO, memberDTO);
            memberDTO.setPhoneNumber(appUserDTO.getPhoneNumber());
            memberDTO.setCountryCode(appUserDTO.getCountryCode());
            memberDTO.setGuardian(guardian);
            memberDTO.setActive(false);
            memberDTO.setDateCreated(Instant.now());
            memberDTO.setUserId(appUserDTO.getId());
            memberDTO = memberService.save(memberDTO);
            if (Boolean.FALSE.equals(memberDTO.getGuardian())) {
                CreateFedimintMemberHttpRequest createFedimintMemberHttpRequest = new CreateFedimintMemberHttpRequest();
                createFedimintMemberHttpRequest.setFederationId(federationDTO.getFedimintId());
                createFedimintMemberHttpRequest.setId(String.valueOf(memberDTO.getId()));
                boolean member = fedimintHttpService.createMember(createFedimintMemberHttpRequest);
                memberDTO.setActive(active);
                memberService.save(memberDTO);
                if (member) {
                    MemberJoinFedimintHttpRequest joinFedimintHttpRequest = new MemberJoinFedimintHttpRequest();
                    joinFedimintHttpRequest.setFederationId(federationDTO.getFedimintId());
                    GetConnectionFedimintHttpResponse getConnectionFedimintHttpResponse = fedimintHttpService.getFederationConnectionString(
                        federationDTO.getFedimintId()
                    );
                    joinFedimintHttpRequest.setConnectionInfo(getConnectionFedimintHttpResponse);
                    String memberId = String.valueOf(memberDTO.getId());
                    fedimintHttpService.joinMember(joinFedimintHttpRequest, memberId);
                }
            }
            queryUtil.persistFederationMember(creatMemberRequestDTO.getFederationId(), memberDTO.getId());
            return memberDTO;
        } else {
            throw new UserException("User not found");
        }
    }

    @Override
    public List<MemberDTO> getAll(Long federationId, Pageable pageable) {
        AppUserDTO loggedInUser = userUtil.getLoggedInUser();
        Optional<MemberDTO> memberByUserIdAndFederationId = queryUtil.getMemberByUserIdAndFederationId(loggedInUser.getId(), federationId);
        if (memberByUserIdAndFederationId.isPresent()) {
            MemberCriteria criteria = new MemberCriteria();
            LongFilter federationIdFilter = new LongFilter();
            federationIdFilter.setEquals(federationId);
            criteria.setFederationId(federationIdFilter);
            return memberQueryService.findByCriteria(criteria, pageable).getContent();
        }
        return new ArrayList<>();
    }

    @Override
    public Optional<MemberDTO> getOne(Long id) {
        return memberService.findOne(id);
    }
}
