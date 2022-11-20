package com.github.agrimint.extended.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class CreateInvoiceHttpRequest {

    private String memberId;

    private String federationId;

    private Long amount;

    private String description;
}
