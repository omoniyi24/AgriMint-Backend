package com.github.agrimint.service;

import com.github.agrimint.service.dto.InviteDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.github.agrimint.domain.Invite}.
 */
public interface InviteService {
    /**
     * Save a invite.
     *
     * @param inviteDTO the entity to save.
     * @return the persisted entity.
     */
    InviteDTO save(InviteDTO inviteDTO);

    /**
     * Partially updates a invite.
     *
     * @param inviteDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<InviteDTO> partialUpdate(InviteDTO inviteDTO);

    /**
     * Get all the invites.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<InviteDTO> findAll(Pageable pageable);

    /**
     * Get the "id" invite.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<InviteDTO> findOne(Long id);

    /**
     * Delete the "id" invite.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
