package com.github.agrimint.extended.dto;

import java.time.Instant;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class AdminAppUserDTO {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String countryCode;

    @NotNull
    private String secret;

    @NotNull
    private String otp;
}
