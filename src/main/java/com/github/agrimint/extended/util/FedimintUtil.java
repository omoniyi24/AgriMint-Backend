package com.github.agrimint.extended.util;

import static com.github.agrimint.extended.util.ApplicationConstants.FEDERATION_IS_NOT_ACTIVE;
import static com.github.agrimint.extended.util.ApplicationConstants.FEDERATION_WITH_ID_DOES_NOT_EXIST;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
import com.github.agrimint.extended.dto.CreateFederationRequestDTO;
import com.github.agrimint.extended.dto.CreateFedimintHttpRequest;
import com.github.agrimint.extended.dto.CreateGuardianFedimintHttpRequest;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.dto.FederationDTO;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
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
        createFedimintHttpRequest.setSecret(String.valueOf(creatGuardianRequestDTO.getSecret()));
        createFedimintHttpRequest.setFederationId(federationDTO.getFedimintId());
        return createFedimintHttpRequest;
    }
}
