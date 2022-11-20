package com.github.agrimint.extended.dto;

import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class PayInvoiceHttpRequest {

    private String memberId;
    private String federationId;
    private String invoice;
}
