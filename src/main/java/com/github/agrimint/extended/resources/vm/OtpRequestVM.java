package com.github.agrimint.extended.resources.vm;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class OtpRequestVM {

    @NotNull
    private String otpType;

    @NotNull
    private int otpLength;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String countryCode;

    private String name;
}
