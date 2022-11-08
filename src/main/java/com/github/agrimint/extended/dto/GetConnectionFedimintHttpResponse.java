package com.github.agrimint.extended.dto;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * @author OMONIYI ILESANMI
 */

@Data
public class GetConnectionFedimintHttpResponse {

    private ArrayList<ArrayList<String>> members;
}
