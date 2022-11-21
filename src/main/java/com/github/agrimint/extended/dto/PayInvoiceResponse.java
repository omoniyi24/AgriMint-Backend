package com.github.agrimint.extended.dto;

import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class PayInvoiceResponse {

    @com.fasterxml.jackson.annotation.JsonProperty("tx_id")
    private String transactionId;
}
