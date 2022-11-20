package com.github.agrimint.extended.dto;

import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class MemberJoinFedimintHttpRequest {

    private String federationId;
    private GetConnectionFedimintHttpResponse connectionInfo;
}
