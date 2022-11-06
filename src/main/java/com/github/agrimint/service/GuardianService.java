package com.github.agrimint.service;

import com.github.agrimint.service.dto.GuardianDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.github.agrimint.domain.Guardian}.
 */
public interface GuardianService {
    /**
     * Save a guardian.
     *
     * @param guardianDTO the entity to save.
     * @return the persisted entity.
     */
    GuardianDTO save(GuardianDTO guardianDTO);

    /**
     * Partially updates a guardian.
     *
     * @param guardianDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<GuardianDTO> partialUpdate(GuardianDTO guardianDTO);

    /**
     * Get all the guardians.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<GuardianDTO> findAll(Pageable pageable);

    /**
     * Get the "id" guardian.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<GuardianDTO> findOne(Long id);

    /**
     * Delete the "id" guardian.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
