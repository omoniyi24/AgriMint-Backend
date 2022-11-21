package com.github.agrimint.extended.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class PayInvoiceDTO {

    @NotBlank
    private String lnInvoice;
}
