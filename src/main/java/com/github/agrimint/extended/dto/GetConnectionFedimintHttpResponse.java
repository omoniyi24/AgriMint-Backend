package com.github.agrimint.extended.dto;

import java.util.List;
import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class GetConnectionFedimintHttpResponse {

    private List<List<String>> members;
}
