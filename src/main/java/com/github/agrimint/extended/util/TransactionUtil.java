package com.github.agrimint.extended.util;

import static com.github.agrimint.domain.enumeration.DRCR.DR;

import com.github.agrimint.domain.enumeration.DRCR;
import com.github.agrimint.domain.enumeration.TransactionType;
import com.github.agrimint.extended.dto.TransferMintRequestDTO;
import com.github.agrimint.extended.dto.TransferMintResponse;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.security.SecurityUtils;
import com.github.agrimint.service.TransactionsService;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.MemberDTO;
import com.github.agrimint.service.dto.TransactionsDTO;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * @author OMONIYI ILESANMI
 */
@Component
public class TransactionUtil {

    private final TransactionsService transactionsService;

    public TransactionUtil(TransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    public void persistTransaction(
        TransferMintRequestDTO transferMintRequestDTO,
        MemberDTO memberDTO,
        TransferMintResponse transferMintResponse,
        DRCR drcr,
        TransactionType transactionType,
        Long federationId
    ) {
        TransactionsDTO transactionsDTO = new TransactionsDTO();
        transactionsDTO.setDescription(transferMintRequestDTO.getDescription());
        transactionsDTO.setTransactionId(transferMintResponse.getTx_id());
        transactionsDTO.setTransactionType(transactionType);
        transactionsDTO.setAmountInSat(transferMintRequestDTO.getAmountInSat());
        transactionsDTO.setDrcr(drcr);
        transactionsDTO.setFederationId(federationId);
        transactionsDTO.setReceiverId(String.valueOf(memberDTO.getId()));
        transactionsDTO.setMemberId(String.valueOf(memberDTO.getId()));
        transactionsDTO.setTransactionDate(Instant.now());
        transactionsService.save(transactionsDTO);
    }
}
