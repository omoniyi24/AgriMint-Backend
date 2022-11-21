package com.github.agrimint.extended.dto;

import javax.validation.constraints.*;
import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class CreateInvoiceDTO {

    @Digits(integer = 21, message = "Invalid amount", fraction = 0)
    private Long amountInSat;

    private String description;
}
