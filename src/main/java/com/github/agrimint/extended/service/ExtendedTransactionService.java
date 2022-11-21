package com.github.agrimint.extended.service;

import com.github.agrimint.domain.Transactions;
import com.github.agrimint.extended.dto.AdminAppUserDTO;
import com.github.agrimint.extended.exception.UserException;
import com.github.agrimint.service.criteria.TransactionsCriteria;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.TransactionsDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author OMONIYI ILESANMI
 */
public interface ExtendedTransactionService {
    Page<TransactionsDTO> findTransaction(TransactionsCriteria criteria, Pageable pageable);
}
