package com.github.agrimint.extended.service;

import com.github.agrimint.extended.resources.vm.OtpRequestVM;
import com.github.agrimint.extended.resources.vm.OtpResponseVM;
import com.github.agrimint.extended.resources.vm.OtpValidationVM;
import com.github.agrimint.service.dto.OtpRequestDTO;

/**
 * @author OMONIYI ILESANMI
 */
public interface ExtendedOtpService {
    OtpResponseVM generateOtp(OtpRequestVM otpRequestVM);

    OtpRequestDTO validateOtp(OtpValidationVM otpResponseVM);
}
