package com.github.agrimint.extended.dto;

import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class CreateFedimintHttpResponse {

    private String _id;
    private String name;
    private Integer nodes;
    private Long basePort;
}
