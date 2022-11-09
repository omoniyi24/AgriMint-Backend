package com.github.agrimint.extended.util;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
import com.github.agrimint.extended.dto.CreateFederationRequestDTO;
import com.github.agrimint.extended.dto.CreateFedimintHttpRequest;
import com.github.agrimint.extended.dto.CreateGuardianFedimintHttpRequest;
import com.github.agrimint.service.dto.FederationDTO;
import org.springframework.stereotype.Component;

/**
 * @author OMONIYI ILESANMI
 */

@Component
public class FedimintUtil {

    public CreateFedimintHttpRequest convertToFedimintRequest(FederationDTO federationDTO) {
        CreateFedimintHttpRequest createFedimintHttpRequest = new CreateFedimintHttpRequest();
        createFedimintHttpRequest.setName(federationDTO.getName());
        createFedimintHttpRequest.setNodes(federationDTO.getNumberOfNode());
        return createFedimintHttpRequest;
    }

    public CreateGuardianFedimintHttpRequest convertToFedimintRequest(
        CreatMemberRequestDTO creatGuardianRequestDTO,
        FederationDTO federationDTO
    ) {
        CreateGuardianFedimintHttpRequest createFedimintHttpRequest = new CreateGuardianFedimintHttpRequest();
        createFedimintHttpRequest.setName(creatGuardianRequestDTO.getName());
        createFedimintHttpRequest.setNode(creatGuardianRequestDTO.getNodeNumber());
        createFedimintHttpRequest.setSecret(creatGuardianRequestDTO.getSecret());
        createFedimintHttpRequest.setFederationId(federationDTO.getFedimintId());
        return createFedimintHttpRequest;
    }
}
