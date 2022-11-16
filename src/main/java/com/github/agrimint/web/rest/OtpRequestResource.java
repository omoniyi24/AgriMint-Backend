package com.github.agrimint.web.rest;

import com.github.agrimint.repository.OtpRequestRepository;
import com.github.agrimint.service.OtpRequestQueryService;
import com.github.agrimint.service.OtpRequestService;
import com.github.agrimint.service.criteria.OtpRequestCriteria;
import com.github.agrimint.service.dto.OtpRequestDTO;
import com.github.agrimint.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.github.agrimint.domain.OtpRequest}.
 */
@RestController
@RequestMapping("/api")
public class OtpRequestResource {

    private final Logger log = LoggerFactory.getLogger(OtpRequestResource.class);

    private static final String ENTITY_NAME = "agriMintOtpRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OtpRequestService otpRequestService;

    private final OtpRequestRepository otpRequestRepository;

    private final OtpRequestQueryService otpRequestQueryService;

    public OtpRequestResource(
        OtpRequestService otpRequestService,
        OtpRequestRepository otpRequestRepository,
        OtpRequestQueryService otpRequestQueryService
    ) {
        this.otpRequestService = otpRequestService;
        this.otpRequestRepository = otpRequestRepository;
        this.otpRequestQueryService = otpRequestQueryService;
    }

    /**
     * {@code POST  /otp-requests} : Create a new otpRequest.
     *
     * @param otpRequestDTO the otpRequestDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new otpRequestDTO, or with status {@code 400 (Bad Request)} if the otpRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/otp-requests")
    public ResponseEntity<OtpRequestDTO> createOtpRequest(@Valid @RequestBody OtpRequestDTO otpRequestDTO) throws URISyntaxException {
        log.debug("REST request to save OtpRequest : {}", otpRequestDTO);
        if (otpRequestDTO.getId() != null) {
            throw new BadRequestAlertException("A new otpRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        OtpRequestDTO result = otpRequestService.save(otpRequestDTO);
        return ResponseEntity
            .created(new URI("/api/otp-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /otp-requests/:id} : Updates an existing otpRequest.
     *
     * @param id the id of the otpRequestDTO to save.
     * @param otpRequestDTO the otpRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated otpRequestDTO,
     * or with status {@code 400 (Bad Request)} if the otpRequestDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the otpRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/otp-requests/{id}")
    public ResponseEntity<OtpRequestDTO> updateOtpRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OtpRequestDTO otpRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to update OtpRequest : {}, {}", id, otpRequestDTO);
        if (otpRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, otpRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!otpRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        OtpRequestDTO result = otpRequestService.save(otpRequestDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, otpRequestDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /otp-requests/:id} : Partial updates given fields of an existing otpRequest, field will ignore if it is null
     *
     * @param id the id of the otpRequestDTO to save.
     * @param otpRequestDTO the otpRequestDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated otpRequestDTO,
     * or with status {@code 400 (Bad Request)} if the otpRequestDTO is not valid,
     * or with status {@code 404 (Not Found)} if the otpRequestDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the otpRequestDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/otp-requests/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<OtpRequestDTO> partialUpdateOtpRequest(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OtpRequestDTO otpRequestDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update OtpRequest partially : {}, {}", id, otpRequestDTO);
        if (otpRequestDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, otpRequestDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!otpRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OtpRequestDTO> result = otpRequestService.partialUpdate(otpRequestDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, otpRequestDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /otp-requests} : get all the otpRequests.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of otpRequests in body.
     */
    @GetMapping("/otp-requests")
    public ResponseEntity<List<OtpRequestDTO>> getAllOtpRequests(OtpRequestCriteria criteria, Pageable pageable) {
        log.debug("REST request to get OtpRequests by criteria: {}", criteria);
        Page<OtpRequestDTO> page = otpRequestQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /otp-requests/count} : count all the otpRequests.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/otp-requests/count")
    public ResponseEntity<Long> countOtpRequests(OtpRequestCriteria criteria) {
        log.debug("REST request to count OtpRequests by criteria: {}", criteria);
        return ResponseEntity.ok().body(otpRequestQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /otp-requests/:id} : get the "id" otpRequest.
     *
     * @param id the id of the otpRequestDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the otpRequestDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/otp-requests/{id}")
    public ResponseEntity<OtpRequestDTO> getOtpRequest(@PathVariable Long id) {
        log.debug("REST request to get OtpRequest : {}", id);
        Optional<OtpRequestDTO> otpRequestDTO = otpRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(otpRequestDTO);
    }

    /**
     * {@code DELETE  /otp-requests/:id} : delete the "id" otpRequest.
     *
     * @param id the id of the otpRequestDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/otp-requests/{id}")
    public ResponseEntity<Void> deleteOtpRequest(@PathVariable Long id) {
        log.debug("REST request to delete OtpRequest : {}", id);
        otpRequestService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
