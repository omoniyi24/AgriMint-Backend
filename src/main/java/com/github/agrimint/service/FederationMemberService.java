package com.github.agrimint.service;

import com.github.agrimint.service.dto.FederationMemberDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.github.agrimint.domain.FederationMember}.
 */
public interface FederationMemberService {
    /**
     * Save a federationMember.
     *
     * @param federationMemberDTO the entity to save.
     * @return the persisted entity.
     */
    FederationMemberDTO save(FederationMemberDTO federationMemberDTO);

    /**
     * Partially updates a federationMember.
     *
     * @param federationMemberDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FederationMemberDTO> partialUpdate(FederationMemberDTO federationMemberDTO);

    /**
     * Get all the federationMembers.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FederationMemberDTO> findAll(Pageable pageable);

    /**
     * Get the "id" federationMember.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FederationMemberDTO> findOne(Long id);

    /**
     * Delete the "id" federationMember.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
