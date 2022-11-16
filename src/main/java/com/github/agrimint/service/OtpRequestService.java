package com.github.agrimint.service;

import com.github.agrimint.service.dto.OtpRequestDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.github.agrimint.domain.OtpRequest}.
 */
public interface OtpRequestService {
    /**
     * Save a otpRequest.
     *
     * @param otpRequestDTO the entity to save.
     * @return the persisted entity.
     */
    OtpRequestDTO save(OtpRequestDTO otpRequestDTO);

    /**
     * Partially updates a otpRequest.
     *
     * @param otpRequestDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OtpRequestDTO> partialUpdate(OtpRequestDTO otpRequestDTO);

    /**
     * Get all the otpRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<OtpRequestDTO> findAll(Pageable pageable);

    /**
     * Get the "id" otpRequest.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OtpRequestDTO> findOne(Long id);

    /**
     * Delete the "id" otpRequest.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
