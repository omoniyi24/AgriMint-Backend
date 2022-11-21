package com.github.agrimint.extended.dto;

import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class TransferMintHttpRequest {

    private String federationId;
    private String senderMemberId;
    private String recipientMemberId;
    private Long amount;
}
