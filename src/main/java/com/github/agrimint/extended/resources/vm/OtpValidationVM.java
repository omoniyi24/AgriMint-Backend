package com.github.agrimint.extended.resources.vm;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OtpValidationVM {

    @NotNull
    private String otpType;

    @NotNull
    private String otp;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String countryCode;
}
