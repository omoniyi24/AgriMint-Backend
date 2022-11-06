package com.github.agrimint.web.rest;

import com.github.agrimint.repository.FederationRepository;
import com.github.agrimint.service.FederationQueryService;
import com.github.agrimint.service.FederationService;
import com.github.agrimint.service.criteria.FederationCriteria;
import com.github.agrimint.service.dto.FederationDTO;
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
 * REST controller for managing {@link com.github.agrimint.domain.Federation}.
 */
@RestController
@RequestMapping("/api")
public class FederationResource {

    private final Logger log = LoggerFactory.getLogger(FederationResource.class);

    private static final String ENTITY_NAME = "agriMintFederation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FederationService federationService;

    private final FederationRepository federationRepository;

    private final FederationQueryService federationQueryService;

    public FederationResource(
        FederationService federationService,
        FederationRepository federationRepository,
        FederationQueryService federationQueryService
    ) {
        this.federationService = federationService;
        this.federationRepository = federationRepository;
        this.federationQueryService = federationQueryService;
    }

    /**
     * {@code POST  /federations} : Create a new federation.
     *
     * @param federationDTO the federationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new federationDTO, or with status {@code 400 (Bad Request)} if the federation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/federations")
    public ResponseEntity<FederationDTO> createFederation(@Valid @RequestBody FederationDTO federationDTO) throws URISyntaxException {
        log.debug("REST request to save Federation : {}", federationDTO);
        if (federationDTO.getId() != null) {
            throw new BadRequestAlertException("A new federation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FederationDTO result = federationService.save(federationDTO);
        return ResponseEntity
            .created(new URI("/api/federations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /federations/:id} : Updates an existing federation.
     *
     * @param id the id of the federationDTO to save.
     * @param federationDTO the federationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated federationDTO,
     * or with status {@code 400 (Bad Request)} if the federationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the federationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/federations/{id}")
    public ResponseEntity<FederationDTO> updateFederation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FederationDTO federationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Federation : {}, {}", id, federationDTO);
        if (federationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, federationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!federationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FederationDTO result = federationService.save(federationDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, federationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /federations/:id} : Partial updates given fields of an existing federation, field will ignore if it is null
     *
     * @param id the id of the federationDTO to save.
     * @param federationDTO the federationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated federationDTO,
     * or with status {@code 400 (Bad Request)} if the federationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the federationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the federationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/federations/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<FederationDTO> partialUpdateFederation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FederationDTO federationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Federation partially : {}, {}", id, federationDTO);
        if (federationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, federationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!federationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FederationDTO> result = federationService.partialUpdate(federationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, federationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /federations} : get all the federations.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of federations in body.
     */
    @GetMapping("/federations")
    public ResponseEntity<List<FederationDTO>> getAllFederations(FederationCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Federations by criteria: {}", criteria);
        Page<FederationDTO> page = federationQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /federations/count} : count all the federations.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/federations/count")
    public ResponseEntity<Long> countFederations(FederationCriteria criteria) {
        log.debug("REST request to count Federations by criteria: {}", criteria);
        return ResponseEntity.ok().body(federationQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /federations/:id} : get the "id" federation.
     *
     * @param id the id of the federationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the federationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/federations/{id}")
    public ResponseEntity<FederationDTO> getFederation(@PathVariable Long id) {
        log.debug("REST request to get Federation : {}", id);
        Optional<FederationDTO> federationDTO = federationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(federationDTO);
    }

    /**
     * {@code DELETE  /federations/:id} : delete the "id" federation.
     *
     * @param id the id of the federationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/federations/{id}")
    public ResponseEntity<Void> deleteFederation(@PathVariable Long id) {
        log.debug("REST request to delete Federation : {}", id);
        federationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
