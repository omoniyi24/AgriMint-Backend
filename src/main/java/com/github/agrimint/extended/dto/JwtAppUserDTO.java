package com.github.agrimint.extended.dto;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JwtAppUserDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String countryCode;

    @NotNull
    private String secret;
}
