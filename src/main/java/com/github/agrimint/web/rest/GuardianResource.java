package com.github.agrimint.web.rest;

import com.github.agrimint.repository.GuardianRepository;
import com.github.agrimint.service.GuardianQueryService;
import com.github.agrimint.service.GuardianService;
import com.github.agrimint.service.criteria.GuardianCriteria;
import com.github.agrimint.service.dto.GuardianDTO;
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
 * REST controller for managing {@link com.github.agrimint.domain.Guardian}.
 */
@RestController
@RequestMapping("/api")
public class GuardianResource {

    private final Logger log = LoggerFactory.getLogger(GuardianResource.class);

    private static final String ENTITY_NAME = "agriMintGuardian";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GuardianService guardianService;

    private final GuardianRepository guardianRepository;

    private final GuardianQueryService guardianQueryService;

    public GuardianResource(
        GuardianService guardianService,
        GuardianRepository guardianRepository,
        GuardianQueryService guardianQueryService
    ) {
        this.guardianService = guardianService;
        this.guardianRepository = guardianRepository;
        this.guardianQueryService = guardianQueryService;
    }

    /**
     * {@code POST  /guardians} : Create a new guardian.
     *
     * @param guardianDTO the guardianDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new guardianDTO, or with status {@code 400 (Bad Request)} if the guardian has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/guardians")
    public ResponseEntity<GuardianDTO> createGuardian(@Valid @RequestBody GuardianDTO guardianDTO) throws URISyntaxException {
        log.debug("REST request to save Guardian : {}", guardianDTO);
        if (guardianDTO.getId() != null) {
            throw new BadRequestAlertException("A new guardian cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GuardianDTO result = guardianService.save(guardianDTO);
        return ResponseEntity
            .created(new URI("/api/guardians/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /guardians/:id} : Updates an existing guardian.
     *
     * @param id the id of the guardianDTO to save.
     * @param guardianDTO the guardianDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated guardianDTO,
     * or with status {@code 400 (Bad Request)} if the guardianDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the guardianDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/guardians/{id}")
    public ResponseEntity<GuardianDTO> updateGuardian(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody GuardianDTO guardianDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Guardian : {}, {}", id, guardianDTO);
        if (guardianDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, guardianDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!guardianRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GuardianDTO result = guardianService.save(guardianDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, guardianDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /guardians/:id} : Partial updates given fields of an existing guardian, field will ignore if it is null
     *
     * @param id the id of the guardianDTO to save.
     * @param guardianDTO the guardianDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated guardianDTO,
     * or with status {@code 400 (Bad Request)} if the guardianDTO is not valid,
     * or with status {@code 404 (Not Found)} if the guardianDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the guardianDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/guardians/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<GuardianDTO> partialUpdateGuardian(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody GuardianDTO guardianDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Guardian partially : {}, {}", id, guardianDTO);
        if (guardianDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, guardianDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!guardianRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GuardianDTO> result = guardianService.partialUpdate(guardianDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, guardianDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /guardians} : get all the guardians.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of guardians in body.
     */
    @GetMapping("/guardians")
    public ResponseEntity<List<GuardianDTO>> getAllGuardians(GuardianCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Guardians by criteria: {}", criteria);
        Page<GuardianDTO> page = guardianQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /guardians/count} : count all the guardians.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/guardians/count")
    public ResponseEntity<Long> countGuardians(GuardianCriteria criteria) {
        log.debug("REST request to count Guardians by criteria: {}", criteria);
        return ResponseEntity.ok().body(guardianQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /guardians/:id} : get the "id" guardian.
     *
     * @param id the id of the guardianDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the guardianDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/guardians/{id}")
    public ResponseEntity<GuardianDTO> getGuardian(@PathVariable Long id) {
        log.debug("REST request to get Guardian : {}", id);
        Optional<GuardianDTO> guardianDTO = guardianService.findOne(id);
        return ResponseUtil.wrapOrNotFound(guardianDTO);
    }

    /**
     * {@code DELETE  /guardians/:id} : delete the "id" guardian.
     *
     * @param id the id of the guardianDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/guardians/{id}")
    public ResponseEntity<Void> deleteGuardian(@PathVariable Long id) {
        log.debug("REST request to delete Guardian : {}", id);
        guardianService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
