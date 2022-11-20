package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.*;
import com.github.agrimint.extended.exception.MemberExecption;
import com.github.agrimint.extended.service.FedimintHttpService;
import com.github.agrimint.extended.service.WalletService;
import com.github.agrimint.extended.util.FederationUtil;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.extended.util.UserUtil;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.FederationDTO;
import com.github.agrimint.service.dto.MemberDTO;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * @author OMONIYI ILESANMI
 */

@Service
public class WalletServiceImpl implements WalletService {

    private final FederationUtil federationUtil;
    private final FedimintHttpService fedimintHttpService;
    private final UserUtil userUtil;
    private final QueryUtil queryUtil;

    public WalletServiceImpl(
        FederationUtil federationUtil,
        FedimintHttpService fedimintHttpService,
        UserUtil userUtil,
        QueryUtil queryUtil
    ) {
        this.federationUtil = federationUtil;
        this.fedimintHttpService = fedimintHttpService;
        this.userUtil = userUtil;
        this.queryUtil = queryUtil;
    }

    @Override
    public MemberHoldingResponse getMemberBalance(Long federationId) {
        FederationDTO federationDTO = federationUtil.getFederation(federationId, true);
        AppUserDTO loggedInUser = userUtil.getLoggedInUser();
        Optional<MemberDTO> memberByUserId = queryUtil.getMemberByUserIdAndFederationId(loggedInUser.getId(), federationDTO.getId());
        if (memberByUserId.isPresent()) {
            return fedimintHttpService.getMemberHoldingInfo(
                String.valueOf(memberByUserId.get().getId()),
                String.valueOf(federationDTO.getId())
            );
        }
        throw new MemberExecption("Federation Member does not exist");
    }

    @Override
    public CreateInvoiceResponse generateInvoice(Long federationId, CreateInvoiceDTO createInvoiceDTO) {
        FederationDTO federationDTO = federationUtil.getFederation(federationId, true);
        AppUserDTO loggedInUser = userUtil.getLoggedInUser();
        Optional<MemberDTO> memberByUserId = queryUtil.getMemberByUserIdAndFederationId(loggedInUser.getId(), federationDTO.getId());
        if (memberByUserId.isPresent()) {
            CreateInvoiceHttpRequest createInvoiceHttpRequest = new CreateInvoiceHttpRequest();
            createInvoiceHttpRequest.setFederationId(federationDTO.getFedimintId());
            createInvoiceHttpRequest.setMemberId(String.valueOf(memberByUserId.get().getId()));
            createInvoiceHttpRequest.setAmount(createInvoiceDTO.getAmountInSat());
            createInvoiceHttpRequest.setDescription(createInvoiceDTO.getDescription());
            return fedimintHttpService.createInvoice(createInvoiceHttpRequest);
        }
        throw new MemberExecption("Federation Member does not exist");
    }

    @Override
    public PayInvoiceResponse payInvoice(Long federationId, PayInvoiceDTO payInvoiceDTO) {
        FederationDTO federationDTO = federationUtil.getFederation(federationId, true);
        AppUserDTO loggedInUser = userUtil.getLoggedInUser();
        Optional<MemberDTO> memberByUserId = queryUtil.getMemberByUserIdAndFederationId(loggedInUser.getId(), federationDTO.getId());
        if (memberByUserId.isPresent()) {
            PayInvoiceHttpRequest payInvoiceHttpRequest = new PayInvoiceHttpRequest();
            payInvoiceHttpRequest.setInvoice(payInvoiceDTO.getLnInvoice());
            payInvoiceHttpRequest.setMemberId(String.valueOf(memberByUserId.get().getId()));
            payInvoiceHttpRequest.setFederationId(federationDTO.getFedimintId());
            return fedimintHttpService.payInvoice(payInvoiceHttpRequest);
        }
        throw new MemberExecption("Federation Member does not exist");
    }
}
