package com.github.agrimint.web.rest;

import com.github.agrimint.repository.PrivilegeRepository;
import com.github.agrimint.service.PrivilegeQueryService;
import com.github.agrimint.service.PrivilegeService;
import com.github.agrimint.service.criteria.PrivilegeCriteria;
import com.github.agrimint.service.dto.PrivilegeDTO;
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
 * REST controller for managing {@link com.github.agrimint.domain.Privilege}.
 */
@RestController
@RequestMapping("/api")
public class PrivilegeResource {

    private final Logger log = LoggerFactory.getLogger(PrivilegeResource.class);

    private static final String ENTITY_NAME = "agriMintPrivilege";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PrivilegeService privilegeService;

    private final PrivilegeRepository privilegeRepository;

    private final PrivilegeQueryService privilegeQueryService;

    public PrivilegeResource(
        PrivilegeService privilegeService,
        PrivilegeRepository privilegeRepository,
        PrivilegeQueryService privilegeQueryService
    ) {
        this.privilegeService = privilegeService;
        this.privilegeRepository = privilegeRepository;
        this.privilegeQueryService = privilegeQueryService;
    }

    /**
     * {@code POST  /privileges} : Create a new privilege.
     *
     * @param privilegeDTO the privilegeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new privilegeDTO, or with status {@code 400 (Bad Request)} if the privilege has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/privileges")
    public ResponseEntity<PrivilegeDTO> createPrivilege(@Valid @RequestBody PrivilegeDTO privilegeDTO) throws URISyntaxException {
        log.debug("REST request to save Privilege : {}", privilegeDTO);
        if (privilegeDTO.getId() != null) {
            throw new BadRequestAlertException("A new privilege cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PrivilegeDTO result = privilegeService.save(privilegeDTO);
        return ResponseEntity
            .created(new URI("/api/privileges/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /privileges/:id} : Updates an existing privilege.
     *
     * @param id the id of the privilegeDTO to save.
     * @param privilegeDTO the privilegeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated privilegeDTO,
     * or with status {@code 400 (Bad Request)} if the privilegeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the privilegeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/privileges/{id}")
    public ResponseEntity<PrivilegeDTO> updatePrivilege(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PrivilegeDTO privilegeDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Privilege : {}, {}", id, privilegeDTO);
        if (privilegeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, privilegeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!privilegeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PrivilegeDTO result = privilegeService.save(privilegeDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, privilegeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /privileges/:id} : Partial updates given fields of an existing privilege, field will ignore if it is null
     *
     * @param id the id of the privilegeDTO to save.
     * @param privilegeDTO the privilegeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated privilegeDTO,
     * or with status {@code 400 (Bad Request)} if the privilegeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the privilegeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the privilegeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/privileges/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<PrivilegeDTO> partialUpdatePrivilege(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PrivilegeDTO privilegeDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Privilege partially : {}, {}", id, privilegeDTO);
        if (privilegeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, privilegeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!privilegeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PrivilegeDTO> result = privilegeService.partialUpdate(privilegeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, privilegeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /privileges} : get all the privileges.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of privileges in body.
     */
    @GetMapping("/privileges")
    public ResponseEntity<List<PrivilegeDTO>> getAllPrivileges(PrivilegeCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Privileges by criteria: {}", criteria);
        Page<PrivilegeDTO> page = privilegeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /privileges/count} : count all the privileges.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/privileges/count")
    public ResponseEntity<Long> countPrivileges(PrivilegeCriteria criteria) {
        log.debug("REST request to count Privileges by criteria: {}", criteria);
        return ResponseEntity.ok().body(privilegeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /privileges/:id} : get the "id" privilege.
     *
     * @param id the id of the privilegeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the privilegeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/privileges/{id}")
    public ResponseEntity<PrivilegeDTO> getPrivilege(@PathVariable Long id) {
        log.debug("REST request to get Privilege : {}", id);
        Optional<PrivilegeDTO> privilegeDTO = privilegeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(privilegeDTO);
    }

    /**
     * {@code DELETE  /privileges/:id} : delete the "id" privilege.
     *
     * @param id the id of the privilegeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/privileges/{id}")
    public ResponseEntity<Void> deletePrivilege(@PathVariable Long id) {
        log.debug("REST request to delete Privilege : {}", id);
        privilegeService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
