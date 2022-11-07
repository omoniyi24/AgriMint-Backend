package com.github.agrimint.extended.service;

import com.github.agrimint.extended.dto.*;
import com.github.agrimint.extended.exeception.FederationExecption;

/**
 * @author OMONIYI ILESANMI
 */
public interface FedimintHttpService {
    CreateFedimintHttpResponse createFedimint(CreateFedimintHttpRequest createFedimintHttpRequest) throws FederationExecption;

    GetConnectionFedimintHttpResponse getFederationConnectionString(String federationId) throws FederationExecption;

    CreateGuardianFedimintHttpResponse createGuardian(CreateGuardianFedimintHttpRequest createFedimintHttpRequest)
        throws FederationExecption;

    GetConnectionFedimintHttpResponse joinFederation(JoinFedimintHttpRequest joinFedimintHttpRequest) throws FederationExecption;

    GetConnectionFedimintHttpResponse exchangeKeys(JoinFedimintHttpRequest joinFedimintHttpRequest) throws FederationExecption;
}
