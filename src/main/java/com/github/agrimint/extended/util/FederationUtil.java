package com.github.agrimint.extended.util;

import static com.github.agrimint.extended.util.ApplicationConstants.FEDERATION_IS_NOT_ACTIVE;
import static com.github.agrimint.extended.util.ApplicationConstants.FEDERATION_WITH_ID_DOES_NOT_EXIST;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
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
public class FederationUtil {

    @Autowired
    private FederationService federationService;

    public FederationDTO getFederation(Long federationId, boolean checkFederation) {
        Optional<FederationDTO> federation = federationService.findOne(federationId);
        if (federation.isEmpty()) {
            throw new FederationExecption(String.format(FEDERATION_WITH_ID_DOES_NOT_EXIST, federationId));
        }
        if (checkFederation && federation.get().getActive().equals(Boolean.FALSE)) {
            throw new FederationExecption(String.format(FEDERATION_IS_NOT_ACTIVE, federationId));
        }
        return federation.get();
    }
}
