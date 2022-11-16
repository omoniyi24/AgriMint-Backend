package com.github.agrimint.extended.util;

import com.github.agrimint.extended.exception.MemberAlreadyExistExecption;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.security.SecurityUtils;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.FederationMemberDTO;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * @author OMONIYI ILESANMI
 */
@Component
public class UserUtil {

    private ExtendedAppUserService extendedAppUserService;

    public AppUserDTO getLoggedInUser() {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        Optional<AppUserDTO> userByPhoneNumberAndCountryCode = extendedAppUserService.findUserByLogin(currentUserLogin.get());
        if (userByPhoneNumberAndCountryCode.isPresent()) {
            return userByPhoneNumberAndCountryCode.get();
        }
        throw new UserException("User not found");
    }
}
