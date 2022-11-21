package com.github.agrimint.service.impl;

import com.github.agrimint.domain.Transactions;
import com.github.agrimint.repository.TransactionsRepository;
import com.github.agrimint.service.TransactionsService;
import com.github.agrimint.service.dto.TransactionsDTO;
import com.github.agrimint.service.mapper.TransactionsMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Transactions}.
 */
@Service
@Transactional
public class TransactionsServiceImpl implements TransactionsService {

    private final Logger log = LoggerFactory.getLogger(TransactionsServiceImpl.class);

    private final TransactionsRepository transactionsRepository;

    private final TransactionsMapper transactionsMapper;

    public TransactionsServiceImpl(TransactionsRepository transactionsRepository, TransactionsMapper transactionsMapper) {
        this.transactionsRepository = transactionsRepository;
        this.transactionsMapper = transactionsMapper;
    }

    @Override
    public TransactionsDTO save(TransactionsDTO transactionsDTO) {
        log.debug("Request to save Transactions : {}", transactionsDTO);
        Transactions transactions = transactionsMapper.toEntity(transactionsDTO);
        transactions = transactionsRepository.save(transactions);
        return transactionsMapper.toDto(transactions);
    }

    @Override
    public Optional<TransactionsDTO> partialUpdate(TransactionsDTO transactionsDTO) {
        log.debug("Request to partially update Transactions : {}", transactionsDTO);

        return transactionsRepository
            .findById(transactionsDTO.getId())
            .map(
                existingTransactions -> {
                    transactionsMapper.partialUpdate(existingTransactions, transactionsDTO);
                    return existingTransactions;
                }
            )
            .map(transactionsRepository::save)
            .map(transactionsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Transactions");
        return transactionsRepository.findAll(pageable).map(transactionsMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionsDTO> findOne(Long id) {
        log.debug("Request to get Transactions : {}", id);
        return transactionsRepository.findById(id).map(transactionsMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Transactions : {}", id);
        transactionsRepository.deleteById(id);
    }
}
