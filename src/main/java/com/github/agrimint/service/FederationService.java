package com.github.agrimint.service;

import com.github.agrimint.service.dto.FederationDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.github.agrimint.domain.Federation}.
 */
public interface FederationService {
    /**
     * Save a federation.
     *
     * @param federationDTO the entity to save.
     * @return the persisted entity.
     */
    FederationDTO save(FederationDTO federationDTO);

    /**
     * Partially updates a federation.
     *
     * @param federationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FederationDTO> partialUpdate(FederationDTO federationDTO);

    /**
     * Get all the federations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FederationDTO> findAll(Pageable pageable);

    /**
     * Get the "id" federation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FederationDTO> findOne(Long id);

    /**
     * Delete the "id" federation.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
