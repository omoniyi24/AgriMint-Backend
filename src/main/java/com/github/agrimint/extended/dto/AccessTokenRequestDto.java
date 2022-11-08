package com.github.agrimint.extended.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Data
public class AccessTokenRequestDto {

    private String countryCode;
    private String phoneNumber;
    private String secret;
}
