package com.github.agrimint.extended.dto;

import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class CreateGuardianFedimintHttpResponse {

    private String _id;
    private Integer node;
    private String federationId;
    private String name;
}
