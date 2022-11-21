package com.github.agrimint.extended.resources;

import com.github.agrimint.domain.Transactions;
import com.github.agrimint.extended.dto.*;
import com.github.agrimint.extended.service.ExtendedTransactionService;
import com.github.agrimint.extended.util.ApplicationUrl;
import com.github.agrimint.service.criteria.TransactionsCriteria;
import com.github.agrimint.service.dto.TransactionsDTO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.PaginationUtil;

/**
 * @author OMONIYI ILESANMI
 */

@RestController
@RequestMapping(ApplicationUrl.BASE_CONTEXT_URL)
public class ExtendedTransactionResource {

    private final Logger log = LoggerFactory.getLogger(ExtendedTransactionResource.class);

    private final ExtendedTransactionService extendedTransactionService;

    public ExtendedTransactionResource(ExtendedTransactionService extendedTransactionService) {
        this.extendedTransactionService = extendedTransactionService;
    }

    @GetMapping("federation/transactions")
    public ResponseEntity<List<TransactionsDTO>> getTransaction(TransactionsCriteria criteria, Pageable pageable) {
        Page<TransactionsDTO> page = extendedTransactionService.findTransaction(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
