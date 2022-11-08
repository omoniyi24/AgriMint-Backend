package com.github.agrimint.service;

import com.github.agrimint.service.dto.PrivilegeDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.github.agrimint.domain.Privilege}.
 */
public interface PrivilegeService {
    /**
     * Save a privilege.
     *
     * @param privilegeDTO the entity to save.
     * @return the persisted entity.
     */
    PrivilegeDTO save(PrivilegeDTO privilegeDTO);

    /**
     * Partially updates a privilege.
     *
     * @param privilegeDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PrivilegeDTO> partialUpdate(PrivilegeDTO privilegeDTO);

    /**
     * Get all the privileges.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PrivilegeDTO> findAll(Pageable pageable);

    /**
     * Get the "id" privilege.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PrivilegeDTO> findOne(Long id);

    /**
     * Delete the "id" privilege.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
