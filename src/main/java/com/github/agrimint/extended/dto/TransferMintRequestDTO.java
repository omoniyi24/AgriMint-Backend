package com.github.agrimint.extended.dto;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class TransferMintRequestDTO {

    @Digits(integer = 21, message = "Invalid amount", fraction = 0)
    private Long recipientMemberId;

    private String description;

    @Digits(integer = 21, message = "Invalid amount", fraction = 0)
    private Long amountInSat;
}
