package com.github.agrimint.service;

import com.github.agrimint.service.dto.TransactionsDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.github.agrimint.domain.Transactions}.
 */
public interface TransactionsService {
    /**
     * Save a transactions.
     *
     * @param transactionsDTO the entity to save.
     * @return the persisted entity.
     */
    TransactionsDTO save(TransactionsDTO transactionsDTO);

    /**
     * Partially updates a transactions.
     *
     * @param transactionsDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TransactionsDTO> partialUpdate(TransactionsDTO transactionsDTO);

    /**
     * Get all the transactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<TransactionsDTO> findAll(Pageable pageable);

    /**
     * Get the "id" transactions.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TransactionsDTO> findOne(Long id);

    /**
     * Delete the "id" transactions.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
