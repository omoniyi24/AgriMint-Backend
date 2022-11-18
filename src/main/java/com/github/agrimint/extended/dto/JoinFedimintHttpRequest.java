package com.github.agrimint.extended.dto;

import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class JoinFedimintHttpRequest {

    private Integer node;
    private String federationId;
    private String secret;
}
