package com.github.agrimint.extended.service;

import com.github.agrimint.extended.dto.CreateFederationRequestDTO;
import com.github.agrimint.extended.dto.CreateFedimintHttpRequest;
import com.github.agrimint.extended.dto.CreateFedimintHttpResponse;
import com.github.agrimint.extended.dto.GetConnectionFedimintHttpResponse;
import com.github.agrimint.extended.exeception.FederationExecption;
import com.github.agrimint.service.criteria.FederationCriteria;
import com.github.agrimint.service.dto.FederationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author OMONIYI ILESANMI
 */
public interface FedimintHttpService {
    CreateFedimintHttpResponse createFedimint(CreateFedimintHttpRequest createFedimintHttpRequest) throws FederationExecption;

    GetConnectionFedimintHttpResponse getFederationConnection(String federationId) throws FederationExecption;
}
