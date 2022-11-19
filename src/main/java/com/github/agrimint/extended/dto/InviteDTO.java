package com.github.agrimint.extended.dto;

import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class InviteDTO {

    private String phoneNumber;
    private String countryCode;
    private Long federationId;
}
