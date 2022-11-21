package com.github.agrimint.extended.util;

import com.github.agrimint.extended.exception.MemberExecption;
import com.github.agrimint.service.*;
import com.github.agrimint.service.criteria.*;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.FederationMemberDTO;
import com.github.agrimint.service.dto.InviteDTO;
import com.github.agrimint.service.dto.MemberDTO;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * @author OMONIYI ILESANMI
 */

@Component
public class QueryUtil {

    private final FederationMemberService federationMemberService;
    private final FederationMemberQueryService federationMemberQueryService;
    private final FederationQueryService federationQueryService;
    private final AppUserQueryService appUserQueryService;
    private final MemberQueryService memberQueryService;
    private final InviteQueryService inviteQueryService;

    public QueryUtil(
        FederationMemberService federationMemberService,
        FederationMemberQueryService federationMemberQueryService,
        FederationQueryService federationQueryService,
        AppUserQueryService appUserQueryService,
        MemberQueryService memberQueryService,
        InviteQueryService inviteQueryService
    ) {
        this.federationMemberService = federationMemberService;
        this.federationMemberQueryService = federationMemberQueryService;
        this.federationQueryService = federationQueryService;
        this.appUserQueryService = appUserQueryService;
        this.memberQueryService = memberQueryService;
        this.inviteQueryService = inviteQueryService;
    }

    public FederationMemberDTO persistFederationMember(Long federationId, Long memberId) throws MemberExecption {
        if (getFederationMemberCount(federationId, memberId) > 0) {
            throw new MemberExecption("Member Already Exist In Federation");
        }
        FederationMemberDTO federationMemberDTO = new FederationMemberDTO();
        federationMemberDTO.setMemberId(memberId);
        federationMemberDTO.setFederationId(federationId);
        federationMemberDTO.setActive(true);
        federationMemberDTO.setDateCreated(Instant.now());
        return federationMemberService.save(federationMemberDTO);
    }

    public Optional<AppUserDTO> getUserByPhoneNumberAndCountryCode(String phoneNumber, String countryCode) {
        AppUserCriteria appUserCriteria = new AppUserCriteria();
        StringFilter phoneNumberFilter = new StringFilter();
        phoneNumberFilter.setEquals(phoneNumber);
        appUserCriteria.setPhoneNumber(phoneNumberFilter);

        StringFilter countryCodeFilter = new StringFilter();
        countryCodeFilter.setEquals(countryCode);
        appUserCriteria.setCountryCode(countryCodeFilter);

        return Optional.ofNullable(appUserQueryService.findByCriteria(appUserCriteria).stream().findFirst().orElse(null));
    }

    public long getFederationCount(String federationName) {
        FederationCriteria federationCriteria = new FederationCriteria();
        StringFilter federationNameFilter = new StringFilter();
        federationNameFilter.setEquals(federationName);
        federationCriteria.setName(federationNameFilter);

        return federationQueryService.countByCriteria(federationCriteria);
    }

    public long getFederationMemberCount(Long federationId, Long memberId) {
        FederationMemberCriteria federationMemberCriteria = new FederationMemberCriteria();
        LongFilter federationIdFilter = new LongFilter();
        federationIdFilter.setEquals(federationId);
        federationMemberCriteria.setFederationId(federationIdFilter);

        LongFilter memberIdFilter = new LongFilter();
        memberIdFilter.setEquals(memberId);
        federationMemberCriteria.setFederationId(memberIdFilter);

        return federationMemberQueryService.countByCriteria(federationMemberCriteria);
    }

    public Optional<FederationMemberDTO> getFederationMember(Long federationId, Long memberId) {
        FederationMemberCriteria federationMemberCriteria = new FederationMemberCriteria();
        LongFilter federationIdFilter = new LongFilter();
        federationIdFilter.setEquals(federationId);
        federationMemberCriteria.setFederationId(federationIdFilter);

        LongFilter memberIdFilter = new LongFilter();
        memberIdFilter.setEquals(memberId);
        federationMemberCriteria.setFederationId(memberIdFilter);

        return Optional.ofNullable(federationMemberQueryService.findByCriteria(federationMemberCriteria).stream().findFirst().orElse(null));
    }

    public Optional<MemberDTO> getMember(Long federationId, Long memberId) {
        MemberCriteria memberCriteria = new MemberCriteria();
        LongFilter federationIdFilter = new LongFilter();
        federationIdFilter.setEquals(federationId);
        memberCriteria.setFederationId(federationIdFilter);

        LongFilter memberIdFilter = new LongFilter();
        memberIdFilter.setEquals(memberId);
        memberCriteria.setId(memberIdFilter);

        return Optional.ofNullable(memberQueryService.findByCriteria(memberCriteria).stream().findFirst().orElse(null));
    }

    public Optional<AppUserDTO> getUserByLogin(String login) {
        AppUserCriteria appUserCriteria = new AppUserCriteria();
        StringFilter loginFilter = new StringFilter();
        loginFilter.setEquals(login);
        appUserCriteria.setLogin(loginFilter);

        return Optional.ofNullable(appUserQueryService.findByCriteria(appUserCriteria).stream().findFirst().orElse(null));
    }

    public List<MemberDTO> getMemberByUserId(Long userId) {
        MemberCriteria memberCriteria = new MemberCriteria();
        LongFilter userIdFilter = new LongFilter();
        userIdFilter.setEquals(userId);
        memberCriteria.setUserId(userIdFilter);

        return memberQueryService.findByCriteria(memberCriteria);
    }

    public Optional<MemberDTO> getMemberByUserIdAndFederationId(Long userId, Long federationId) {
        MemberCriteria memberCriteria = new MemberCriteria();
        LongFilter userIdFilter = new LongFilter();
        userIdFilter.setEquals(userId);
        memberCriteria.setUserId(userIdFilter);

        LongFilter federationIdFilter = new LongFilter();
        federationIdFilter.setEquals(federationId);
        memberCriteria.setFederationId(federationIdFilter);

        return Optional.ofNullable(memberQueryService.findByCriteria(memberCriteria).stream().findFirst().orElse(null));
    }

    public Optional<InviteDTO> getInvitationCode(String phoneNumber, String countryCode, String code) {
        InviteCriteria inviteCriteria = new InviteCriteria();
        StringFilter phoneNumberFilter = new StringFilter();
        phoneNumberFilter.setEquals(phoneNumber);
        inviteCriteria.setPhoneNumber(phoneNumberFilter);

        StringFilter countryCodeFilter = new StringFilter();
        countryCodeFilter.setEquals(countryCode);
        inviteCriteria.setCountryCode(countryCodeFilter);

        StringFilter invitationCodeFilter = new StringFilter();
        invitationCodeFilter.setEquals(code);
        inviteCriteria.setInvitationCode(invitationCodeFilter);

        BooleanFilter activeFilter = new BooleanFilter();
        activeFilter.setEquals(true);
        inviteCriteria.setActive(activeFilter);

        return Optional.ofNullable(inviteQueryService.findByCriteria(inviteCriteria).stream().findFirst().orElse(null));
    }
}
