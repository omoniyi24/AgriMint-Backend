package com.github.agrimint.extended.service.impl;

import com.github.agrimint.extended.dto.PayInvoiceHttpRequest;
import com.github.agrimint.extended.exception.MemberExecption;
import com.github.agrimint.extended.service.ExtendedAppUserService;
import com.github.agrimint.extended.service.ExtendedTransactionService;
import com.github.agrimint.extended.util.FederationUtil;
import com.github.agrimint.extended.util.QueryUtil;
import com.github.agrimint.extended.util.UserUtil;
import com.github.agrimint.security.SecurityUtils;
import com.github.agrimint.service.TransactionsQueryService;
import com.github.agrimint.service.criteria.TransactionsCriteria;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.dto.FederationDTO;
import com.github.agrimint.service.dto.MemberDTO;
import com.github.agrimint.service.dto.TransactionsDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * @author OMONIYI ILESANMI
 */
@Service
public class ExtendedTransactionServiceImpl implements ExtendedTransactionService {

    private final TransactionsQueryService transactionsQueryService;
    private final ExtendedAppUserService extendedAppUserService;
    private final FederationUtil federationUtil;
    private final UserUtil userUtil;
    private final QueryUtil queryUtil;

    public ExtendedTransactionServiceImpl(
        TransactionsQueryService transactionsQueryService,
        ExtendedAppUserService extendedAppUserService,
        FederationUtil federationUtil,
        UserUtil userUtil,
        QueryUtil queryUtil
    ) {
        this.transactionsQueryService = transactionsQueryService;
        this.extendedAppUserService = extendedAppUserService;
        this.federationUtil = federationUtil;
        this.userUtil = userUtil;
        this.queryUtil = queryUtil;
    }

    @Override
    public Page<TransactionsDTO> findTransaction(TransactionsCriteria criteria, Pageable pageable) {
        FederationDTO federationDTO = federationUtil.getFederation(criteria.federationId().getEquals(), false);
        AppUserDTO loggedInUser = userUtil.getLoggedInUser();
        Optional<MemberDTO> memberByUserId = queryUtil.getMemberByUserIdAndFederationId(loggedInUser.getId(), federationDTO.getId());
        if (memberByUserId.isPresent()) {
            StringFilter memberIdFilter = new StringFilter();
            memberIdFilter.setEquals(String.valueOf(memberByUserId.get().getId()));
            criteria.setMemberId(memberIdFilter);

            LongFilter federationIdFilter = new LongFilter();
            federationIdFilter.setEquals(federationDTO.getId());
            criteria.setFederationId(federationIdFilter);

            return transactionsQueryService.findByCriteria(criteria, pageable);
        }
        throw new MemberExecption("Federation Member does not exist");
    }
}
