package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.ExtendedInviteDTO;
import com.github.agrimint.extended.dto.SmsRequestDTO;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.MemberExecption;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.extended.service.ExtendedInviteService;
import com.github.agrimint.extended.service.SMSHttpService;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.security.SecurityUtils;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.InviteService;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.FederationDTO;
import com.github.agrimint.service.dto.InviteDTO;
import com.github.agrimint.service.dto.MemberDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author OMONIYI ILESANMI
 */

@Service
public class ExtendedInviteServiceImpl implements ExtendedInviteService {

    @Value("${notification.template.invitation}")
    private String invitationTemplate;

    private final ExtendedAppUserService extendedAppUserService;
    private final FederationService federationService;
    private final QueryUtil queryUtil;
    private final InviteService inviteService;
    private final SMSHttpService smsHttpService;

    public ExtendedInviteServiceImpl(
        ExtendedAppUserService extendedAppUserService,
        FederationService federationService,
        QueryUtil queryUtil,
        InviteService inviteService,
        SMSHttpService smsHttpService
    ) {
        this.extendedAppUserService = extendedAppUserService;
        this.federationService = federationService;
        this.queryUtil = queryUtil;
        this.inviteService = inviteService;
        this.smsHttpService = smsHttpService;
    }

    @Override
    public Map<String, String> send(ExtendedInviteDTO extendedInviteDTO) {
        Map<String, String> response = new HashMap<>();
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        Optional<AppUserDTO> userByPhoneNumberAndCountryCode = extendedAppUserService.findUserByLogin(currentUserLogin.get());
        if (userByPhoneNumberAndCountryCode.isPresent()) {
            Optional<FederationDTO> federationDTO = federationService.findOne(extendedInviteDTO.getFederationId());
            if (federationDTO.isEmpty()) {
                throw new FederationExecption("Federation not found");
            }

            AppUserDTO appUserDTO = userByPhoneNumberAndCountryCode.get();
            Optional<MemberDTO> memberByUserId = queryUtil.getMemberByUserIdAndFederationId(
                appUserDTO.getId(),
                extendedInviteDTO.getFederationId()
            );
            if (memberByUserId.isPresent()) {
                MemberDTO memberDTO = memberByUserId.get();
                InviteDTO inviteDTO = new InviteDTO();
                inviteDTO.setPhoneNumber(extendedInviteDTO.getPhoneNumber());
                inviteDTO.setCountryCode(extendedInviteDTO.getCountryCode());
                inviteDTO.setInvitationCode(RandomStringUtils.randomNumeric(6));
                inviteDTO.setActive(true);
                inviteDTO.setFederationId(memberDTO.getFederationId());
                inviteService.save(inviteDTO);
                String id = String.format("%s", inviteDTO.getInvitationCode());
                response.put("invitationCode", id);

                SmsRequestDTO smsRequestDTO = new SmsRequestDTO();
                smsRequestDTO.setCountryCode(inviteDTO.getCountryCode());
                smsRequestDTO.setPhoneNumber(inviteDTO.getPhoneNumber());
                String message = String.format(invitationTemplate, federationDTO.get().getName(), id);
                smsRequestDTO.setMessage(message);
                smsHttpService.send(smsRequestDTO);
            } else {
                throw new MemberExecption("Member not found");
            }
        } else {
            throw new UserException("User not found");
        }
        return response;
    }
}
