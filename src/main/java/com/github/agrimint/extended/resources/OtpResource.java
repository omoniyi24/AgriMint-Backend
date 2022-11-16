package com.github.agrimint.extended.resources;

import com.github.agrimint.extended.dto.SmsRequestDTO;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.resources.vm.OtpRequestVM;
import com.github.agrimint.extended.resources.vm.OtpResponseVM;
import com.github.agrimint.extended.resources.vm.OtpValidationVM;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.extended.service.ExtendedOtpService;
import com.github.agrimint.extended.service.SMSHttpService;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.OtpRequestDTO;
import java.util.Optional;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/otp")
public class OtpResource {

    private final ExtendedOtpService extendedOtpService;
    private final ExtendedAppUserService extendedAppUserService;
    private final SMSHttpService smsHttpService;

    public OtpResource(
        ExtendedOtpService extendedOtpService,
        ExtendedAppUserService extendedAppUserService,
        SMSHttpService smsHttpService
    ) {
        this.extendedOtpService = extendedOtpService;
        this.extendedAppUserService = extendedAppUserService;
        this.smsHttpService = smsHttpService;
    }

    @PostMapping("/request")
    public ResponseEntity<OtpResponseVM> generateOtp(@Valid @RequestBody OtpRequestVM otpRequestVM) throws Exception {
        Optional<AppUserDTO> userByPhoneNumberAndCountryCode = extendedAppUserService.findUserByPhoneNumberAndCountryCode(
            otpRequestVM.getPhoneNumber(),
            otpRequestVM.getCountryCode()
        );
        if (userByPhoneNumberAndCountryCode.isPresent()) {
            throw new UserException("User Already Exist");
        }
        OtpResponseVM otpResponseVM = extendedOtpService.generateOtp(otpRequestVM);
        SmsRequestDTO smsRequestDTO = new SmsRequestDTO();
        smsRequestDTO.setCountryCode(otpRequestVM.getCountryCode());
        smsRequestDTO.setPhoneNumber(otpRequestVM.getPhoneNumber());
        smsRequestDTO.setName(StringUtils.defaultIfEmpty(otpRequestVM.getName(), "Customer"));
        smsRequestDTO.setMessage(otpResponseVM.getOtp());
        smsHttpService.send(smsRequestDTO);
        return ResponseEntity.ok().body(otpResponseVM);
    }

    @PostMapping("/validate")
    public ResponseEntity<OtpValidationVM> validateOtp(@Valid @RequestBody OtpValidationVM otpValidationVM) throws Exception {
        OtpRequestDTO otpRequestDTO = extendedOtpService.validateOtp(otpValidationVM);
        if (otpRequestDTO != null && otpRequestDTO.getStatus().equals("DONE")) {
            return ResponseEntity.ok().body(otpValidationVM);
        }
        return ResponseEntity.badRequest().build();
    }
}
