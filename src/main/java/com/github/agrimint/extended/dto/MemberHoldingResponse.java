package com.github.agrimint.extended.dto;

import java.util.Map;
import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class MemberHoldingResponse {

    @com.fasterxml.jackson.annotation.JsonProperty("total_amount")
    private long totalAmount;

    @com.fasterxml.jackson.annotation.JsonProperty("total_num_notes")
    private long totalNumNotes;

    @com.fasterxml.jackson.annotation.JsonProperty("details")
    private Map<String, String> details;
}
