package com.github.agrimint.extended.dto;

import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class CreateGuardianFedimintHttpRequest {

    private String name;
    private Integer node;
    private String federationId;
    private String secret;
}
