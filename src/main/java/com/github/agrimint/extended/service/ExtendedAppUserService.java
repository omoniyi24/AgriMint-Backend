package com.github.agrimint.extended.service;

import com.github.agrimint.domain.AppUser;
import com.github.agrimint.extended.dto.AdminAppUserDTO;
import com.github.agrimint.extended.exeception.UserException;
import com.github.agrimint.service.dto.AppUserDTO;
import java.util.Optional;

/**
 * @author OMONIYI ILESANMI
 */
public interface ExtendedAppUserService {
    Optional<AppUserDTO> findUserByPhoneNumberAndCountryCode(String countryCode, String phoneNumber);

    AppUserDTO createAppUser(AdminAppUserDTO userDTO) throws UserException;

    Optional<AppUserDTO> findUserByLogin(String login);
}
