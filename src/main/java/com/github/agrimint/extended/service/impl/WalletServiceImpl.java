package com.github.agrimint.extended.service.impl;

import static com.github.agrimint.domain.enumeration.DRCR.CR;
import static com.github.agrimint.domain.enumeration.DRCR.DR;
import static com.github.agrimint.domain.enumeration.TransactionType.ON_MINT;

import com.github.agrimint.domain.Transactions;
import com.github.agrimint.domain.enumeration.DRCR;
import com.github.agrimint.domain.enumeration.TransactionType;
import com.github.agrimint.extended.dto.*;
import com.github.agrimint.extended.exception.MemberExecption;
import com.github.agrimint.extended.service.FedimintHttpService;
import com.github.agrimint.extended.service.WalletService;
import com.github.agrimint.extended.util.FederationUtil;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.extended.util.TransactionUtil;
import com.github.agrimint.extended.util.UserUtil;
import com.github.agrimint.service.TransactionsService;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.FederationDTO;
import com.github.agrimint.service.dto.MemberDTO;
import com.github.agrimint.service.dto.TransactionsDTO;
import java.time.Instant;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
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
    private final TransactionUtil transactionUtil;

    public WalletServiceImpl(
        FederationUtil federationUtil,
        FedimintHttpService fedimintHttpService,
        UserUtil userUtil,
        QueryUtil queryUtil,
        TransactionUtil transactionUtil
    ) {
        this.federationUtil = federationUtil;
        this.fedimintHttpService = fedimintHttpService;
        this.userUtil = userUtil;
        this.queryUtil = queryUtil;
        this.transactionUtil = transactionUtil;
    }

    @Override
    public MemberHoldingResponse getMemberBalance(Long federationId) {
        FederationDTO federationDTO = federationUtil.getFederation(federationId, true);
        AppUserDTO loggedInUser = userUtil.getLoggedInUser();
        Optional<MemberDTO> memberByUserId = queryUtil.getMemberByUserIdAndFederationId(loggedInUser.getId(), federationDTO.getId());
        if (memberByUserId.isPresent()) {
            return fedimintHttpService.getMemberHoldingInfo(String.valueOf(memberByUserId.get().getId()), federationDTO.getFedimintId());
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

    @Override
    public TransferMintResponse transferMint(Long federationId, TransferMintRequestDTO transferMintRequestDTO) {
        FederationDTO federationDTO = federationUtil.getFederation(federationId, true);
        AppUserDTO loggedInUser = userUtil.getLoggedInUser();
        Optional<MemberDTO> memberByUserId = queryUtil.getMemberByUserIdAndFederationId(loggedInUser.getId(), federationDTO.getId());
        if (memberByUserId.isPresent()) {
            Optional<MemberDTO> recipientByUserId = queryUtil.getMember(
                federationDTO.getId(),
                transferMintRequestDTO.getRecipientMemberId()
            );
            if (recipientByUserId.isPresent() && recipientByUserId.get().getActive()) {
                transferMintRequestDTO.setDescription(StringUtils.defaultIfBlank(transferMintRequestDTO.getDescription(), "Transfer Mint"));
                MemberDTO loggedInMemberDTO = memberByUserId.get();
                MemberDTO recipientMemberDTO = recipientByUserId.get();
                TransferMintHttpRequest transferMintHttpRequest = new TransferMintHttpRequest();
                transferMintHttpRequest.setAmount(transferMintRequestDTO.getAmountInSat());
                transferMintHttpRequest.setFederationId(federationDTO.getFedimintId());
                transferMintHttpRequest.setRecipientMemberId(String.valueOf(recipientMemberDTO.getId()));
                transferMintHttpRequest.setSenderMemberId(String.valueOf(loggedInMemberDTO.getId()));
                TransferMintResponse transferMintResponse = fedimintHttpService.transferMint(transferMintHttpRequest);
                if (StringUtils.isNotBlank(transferMintResponse.getTx_id())) {
                    transactionUtil.persistTransaction(
                        transferMintRequestDTO,
                        loggedInMemberDTO,
                        transferMintResponse,
                        DR,
                        ON_MINT,
                        federationId
                    );
                    transactionUtil.persistTransaction(
                        transferMintRequestDTO,
                        recipientMemberDTO,
                        transferMintResponse,
                        CR,
                        ON_MINT,
                        federationId
                    );
                }
                return transferMintResponse;
            }
            throw new MemberExecption("Recipient does not exist in Federation");
        }
        throw new MemberExecption("Federation Member does not exist");
    }
}
