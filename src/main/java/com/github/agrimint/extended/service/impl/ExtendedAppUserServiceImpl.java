package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.AdminAppUserDTO;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.resources.vm.OtpValidationVM;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.extended.service.ExtendedOtpService;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.repository.AppUserRepository;
import com.github.agrimint.service.AppUserService;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.MemberDTO;
import com.github.agrimint.service.dto.OtpRequestDTO;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author OMONIYI ILESANMI
 */

@Service
@Slf4j
public class ExtendedAppUserServiceImpl implements ExtendedAppUserService {

    private final QueryUtil queryUtil;
    private final PasswordEncoder passwordEncoder;
    private final AppUserService appUserService;
    private final ExtendedOtpService extendedOtpService;

    public ExtendedAppUserServiceImpl(
        QueryUtil queryUtil,
        PasswordEncoder passwordEncoder,
        AppUserService appUserService,
        ExtendedOtpService extendedOtpService
    ) {
        this.queryUtil = queryUtil;
        this.passwordEncoder = passwordEncoder;
        this.appUserService = appUserService;
        this.extendedOtpService = extendedOtpService;
    }

    @Override
    public Optional<AppUserDTO> findUserByPhoneNumberAndCountryCode(String phoneNumber, String countryCode) {
        return queryUtil.getUserByPhoneNumberAndCountryCode(phoneNumber, countryCode);
    }

    @Override
    public Optional<AppUserDTO> findUserByLogin(String login) {
        return queryUtil.getUserByLogin(login);
    }

    @Override
    public AppUserDTO createAppUser(AdminAppUserDTO userDTO, String channel) throws UserException {
        if (StringUtils.isBlank(channel) || !channel.equalsIgnoreCase("USSD")) {
            OtpValidationVM otpResponseVM = new OtpValidationVM();
            otpResponseVM.setOtp(userDTO.getOtp());
            otpResponseVM.setOtpType("REG");
            otpResponseVM.setCountryCode(userDTO.getCountryCode());
            otpResponseVM.setPhoneNumber(userDTO.getPhoneNumber());
            OtpRequestDTO otpRequestDTO = extendedOtpService.validateOtp(otpResponseVM);
            if (otpRequestDTO == null || !otpRequestDTO.getStatus().equals("DONE")) {
                throw new UserException("Invalid OTP");
            }
        }

        AppUserDTO newAppUserDTO = new AppUserDTO();
        String encryptedSecret = passwordEncoder.encode(userDTO.getSecret());
        newAppUserDTO.setLogin(userDTO.getCountryCode() + userDTO.getPhoneNumber());
        newAppUserDTO.setPhoneNumber(userDTO.getPhoneNumber());
        newAppUserDTO.setSecret(encryptedSecret);
        newAppUserDTO.setActive(true);
        newAppUserDTO.setDateCreated(Instant.now());
        newAppUserDTO.setName(userDTO.getName());
        newAppUserDTO.setCountryCode(userDTO.getCountryCode());
        //        Set<Authority> authorities = new HashSet<>();
        //        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        //        newUser.setAuthorities(authorities);
        appUserService.save(newAppUserDTO);
        log.debug("Created Information for User: {}", newAppUserDTO);
        return newAppUserDTO;
    }
}
