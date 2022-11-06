package com.github.agrimint.extended.util;

import com.github.agrimint.extended.dto.CreateFederationRequestDTO;
import com.github.agrimint.extended.dto.CreateFedimintHttpRequest;
import org.springframework.stereotype.Component;

/**
 * @author OMONIYI ILESANMI
 */

@Component
public class FedimintUtil {

    public CreateFedimintHttpRequest convertToFedimintRequest(CreateFederationRequestDTO createFederationRequestDTO) {
        CreateFedimintHttpRequest createFedimintHttpRequest = new CreateFedimintHttpRequest();
        createFedimintHttpRequest.setName(createFederationRequestDTO.getName());
        createFedimintHttpRequest.setNodes(createFederationRequestDTO.getNumberOfNode());
        return createFedimintHttpRequest;
    }
}
