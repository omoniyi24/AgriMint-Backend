package com.github.agrimint.extended.util;

import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.security.SecurityUtils;
import com.github.agrimint.service.dto.AppUserDTO;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * @author OMONIYI ILESANMI
 */
@Component
public class UserUtil {

    private final ExtendedAppUserService extendedAppUserService;

    public UserUtil(ExtendedAppUserService extendedAppUserService) {
        this.extendedAppUserService = extendedAppUserService;
    }

    public AppUserDTO getLoggedInUser() {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        Optional<AppUserDTO> userByPhoneNumberAndCountryCode = extendedAppUserService.findUserByLogin(currentUserLogin.get());
        if (userByPhoneNumberAndCountryCode.isPresent()) {
            return userByPhoneNumberAndCountryCode.get();
        }
        throw new UserException("User not found");
    }
}
