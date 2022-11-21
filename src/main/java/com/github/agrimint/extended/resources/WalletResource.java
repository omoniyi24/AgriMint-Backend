package com.github.agrimint.extended.resources;

import com.github.agrimint.extended.dto.*;
import com.github.agrimint.extended.service.WalletService;
import com.github.agrimint.extended.util.ApplicationUrl;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author OMONIYI ILESANMI
 */

@RestController
@RequestMapping(ApplicationUrl.BASE_CONTEXT_URL + "/wallet")
public class WalletResource {

    private final Logger log = LoggerFactory.getLogger(WalletResource.class);

    private final WalletService walletService;

    public WalletResource(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("federation/{federationId}/balance")
    public ResponseEntity<MemberHoldingResponse> getUserFederationWalletBalance(@PathVariable Long federationId) {
        log.debug("REST getUserFederationWalletBalance : {}", federationId);
        MemberHoldingResponse memberBalance = walletService.getMemberBalance(federationId);
        return ResponseEntity.ok(memberBalance);
    }

    @PostMapping("federation/{federationId}/invoice")
    public ResponseEntity<CreateInvoiceResponse> generateFederationUserInvoice(
        @PathVariable Long federationId,
        @Valid @RequestBody CreateInvoiceDTO createInvoiceDTO
    ) {
        log.debug("REST generateFederationUserInvoice : {}", federationId);
        CreateInvoiceResponse createInvoiceResponse = walletService.generateInvoice(federationId, createInvoiceDTO);
        return ResponseEntity.ok(createInvoiceResponse);
    }

    @PostMapping("federation/{federationId}/pay")
    public ResponseEntity<PayInvoiceResponse> payFederationUserInvoice(
        @PathVariable Long federationId,
        @Valid @RequestBody PayInvoiceDTO payInvoiceDTO
    ) {
        log.debug("REST payFederationUserInvoice : {}", payInvoiceDTO);
        PayInvoiceResponse payInvoiceResponse = walletService.payInvoice(federationId, payInvoiceDTO);
        return ResponseEntity.ok(payInvoiceResponse);
    }

    @PostMapping("federation/{federationId}/transfer-mint")
    public ResponseEntity<TransferMintResponse> transferMint(
        @PathVariable Long federationId,
        @Valid @RequestBody TransferMintRequestDTO transferMintRequestDTO
    ) {
        log.debug("REST transferMint : {}", transferMintRequestDTO);
        TransferMintResponse transferMintResponse = walletService.transferMint(federationId, transferMintRequestDTO);
        return ResponseEntity.ok(transferMintResponse);
    }
}
