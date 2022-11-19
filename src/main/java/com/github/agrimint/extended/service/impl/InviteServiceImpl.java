package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.InviteDTO;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.MemberExecption;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.extended.service.InviteService;
import com.github.agrimint.extended.util.Base64Util;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.security.SecurityUtils;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.FederationDTO;
import com.github.agrimint.service.dto.MemberDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

/**
 * @author OMONIYI ILESANMI
 */

@Service
public class InviteServiceImpl implements InviteService {

    private final ExtendedAppUserService extendedAppUserService;
    private final FederationService federationService;
    private final QueryUtil queryUtil;

    public InviteServiceImpl(ExtendedAppUserService extendedAppUserService, FederationService federationService, QueryUtil queryUtil) {
        this.extendedAppUserService = extendedAppUserService;
        this.federationService = federationService;
        this.queryUtil = queryUtil;
    }

    @Override
    public Map<String, String> send(InviteDTO inviteDTO) {
        Map<String, String> response = new HashMap<>();
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        Optional<AppUserDTO> userByPhoneNumberAndCountryCode = extendedAppUserService.findUserByLogin(currentUserLogin.get());
        if (userByPhoneNumberAndCountryCode.isPresent()) {
            Optional<FederationDTO> federationDTO = federationService.findOne(inviteDTO.getFederationId());
            if (federationDTO.isEmpty()) {
                throw new FederationExecption("Federation not found");
            }

            AppUserDTO appUserDTO = userByPhoneNumberAndCountryCode.get();
            Optional<MemberDTO> memberByUserId = queryUtil.getMemberByUserIdAndFederationId(
                appUserDTO.getId(),
                inviteDTO.getFederationId()
            );
            if (memberByUserId.isPresent()) {
                MemberDTO memberDTO = memberByUserId.get();
                long random = System.currentTimeMillis();
                String id = String.format("%s-%s", random, memberDTO.getFederationId());
                String base64Response = Base64Util.ecryptTeamActivationKey(id);
                response.put("id", base64Response);
            } else {
                throw new MemberExecption("Member not found");
            }
        } else {
            throw new UserException("User not found");
        }
        return response;
    }
}
