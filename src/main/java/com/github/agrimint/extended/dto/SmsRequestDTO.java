package com.github.agrimint.extended.dto;

import lombok.Data;

@Data
public class SmsRequestDTO {

    private String message;
    private String phoneNumber;
    private String countryCode;
    private String name;
}
