package com.github.agrimint.extended.service;

import com.github.agrimint.extended.dto.*;
import com.github.agrimint.extended.exception.FederationExecption;

/**
 * @author OMONIYI ILESANMI
 */
public interface FedimintHttpService {
    CreateFedimintHttpResponse createFedimint(CreateFedimintHttpRequest createFedimintHttpRequest) throws FederationExecption;

    GetConnectionFedimintHttpResponse getFederationConnectionString(String federationId) throws FederationExecption;

    CreateGuardianFedimintHttpResponse createGuardian(CreateGuardianFedimintHttpRequest createFedimintHttpRequest)
        throws FederationExecption;

    boolean joinFederation(JoinFedimintHttpRequest joinFedimintHttpRequest, String guadianFedimintId) throws FederationExecption;

    boolean exchangeKeys(JoinFedimintHttpRequest joinFedimintHttpRequest, String guadianFedimintId) throws FederationExecption;

    boolean createMember(CreateFedimintMemberHttpRequest createFedimintMemberHttpRequest);

    boolean joinMember(MemberJoinFedimintHttpRequest joinFedimintHttpRequest, String memberId);

    MemberHoldingResponse getMemberHoldingInfo(String memberId, String federationId);

    CreateInvoiceResponse createInvoice(CreateInvoiceHttpRequest createInvoiceHttpRequest);

    PayInvoiceResponse payInvoice(PayInvoiceHttpRequest payInvoiceHttpRequest);
}
