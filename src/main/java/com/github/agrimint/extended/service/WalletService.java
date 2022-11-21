package com.github.agrimint.extended.service;

import com.github.agrimint.extended.dto.*;

/**
 * @author OMONIYI ILESANMI
 */

public interface WalletService {
    MemberHoldingResponse getMemberBalance(Long federationId);

    CreateInvoiceResponse generateInvoice(Long federationId, CreateInvoiceDTO createInvoiceDTO);

    PayInvoiceResponse payInvoice(Long federationId, PayInvoiceDTO payInvoiceDTO);

    TransferMintResponse transferMint(Long federationId, TransferMintRequestDTO transferMintRequestDTO);
}
