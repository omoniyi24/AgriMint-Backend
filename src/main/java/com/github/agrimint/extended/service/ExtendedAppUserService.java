package com.github.agrimint.extended.service;

import com.github.agrimint.extended.dto.AdminAppUserDTO;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.service.dto.AppUserDTO;
import java.util.Optional;

/**
 * @author OMONIYI ILESANMI
 */
public interface ExtendedAppUserService {
    Optional<AppUserDTO> findUserByPhoneNumberAndCountryCode(String phoneNumber, String countryCode);

    AppUserDTO createAppUser(AdminAppUserDTO userDTO) throws UserException;

    Optional<AppUserDTO> findUserByLogin(String login);
}
