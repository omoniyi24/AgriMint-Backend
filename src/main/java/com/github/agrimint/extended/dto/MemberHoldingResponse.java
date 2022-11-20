package com.github.agrimint.extended.dto;

import java.util.Map;
import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class MemberHoldingResponse {

    @com.fasterxml.jackson.annotation.JsonProperty("total_amount")
    private Long totalAmount;

    @com.fasterxml.jackson.annotation.JsonProperty("total_num_notes")
    private Long totalNumNotes;

    @com.fasterxml.jackson.annotation.JsonProperty("details")
    private Map<String, String> details;
}
