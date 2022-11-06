package com.github.agrimint.web.rest;

import com.github.agrimint.repository.FederationMemberRepository;
import com.github.agrimint.service.FederationMemberQueryService;
import com.github.agrimint.service.FederationMemberService;
import com.github.agrimint.service.criteria.FederationMemberCriteria;
import com.github.agrimint.service.dto.FederationMemberDTO;
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
 * REST controller for managing {@link com.github.agrimint.domain.FederationMember}.
 */
@RestController
@RequestMapping("/api")
public class FederationMemberResource {

    private final Logger log = LoggerFactory.getLogger(FederationMemberResource.class);

    private static final String ENTITY_NAME = "agriMintFederationMember";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FederationMemberService federationMemberService;

    private final FederationMemberRepository federationMemberRepository;

    private final FederationMemberQueryService federationMemberQueryService;

    public FederationMemberResource(
        FederationMemberService federationMemberService,
        FederationMemberRepository federationMemberRepository,
        FederationMemberQueryService federationMemberQueryService
    ) {
        this.federationMemberService = federationMemberService;
        this.federationMemberRepository = federationMemberRepository;
        this.federationMemberQueryService = federationMemberQueryService;
    }

    /**
     * {@code POST  /federation-members} : Create a new federationMember.
     *
     * @param federationMemberDTO the federationMemberDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new federationMemberDTO, or with status {@code 400 (Bad Request)} if the federationMember has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/federation-members")
    public ResponseEntity<FederationMemberDTO> createFederationMember(@Valid @RequestBody FederationMemberDTO federationMemberDTO)
        throws URISyntaxException {
        log.debug("REST request to save FederationMember : {}", federationMemberDTO);
        if (federationMemberDTO.getId() != null) {
            throw new BadRequestAlertException("A new federationMember cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FederationMemberDTO result = federationMemberService.save(federationMemberDTO);
        return ResponseEntity
            .created(new URI("/api/federation-members/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /federation-members/:id} : Updates an existing federationMember.
     *
     * @param id the id of the federationMemberDTO to save.
     * @param federationMemberDTO the federationMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated federationMemberDTO,
     * or with status {@code 400 (Bad Request)} if the federationMemberDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the federationMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/federation-members/{id}")
    public ResponseEntity<FederationMemberDTO> updateFederationMember(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FederationMemberDTO federationMemberDTO
    ) throws URISyntaxException {
        log.debug("REST request to update FederationMember : {}, {}", id, federationMemberDTO);
        if (federationMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, federationMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!federationMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FederationMemberDTO result = federationMemberService.save(federationMemberDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, federationMemberDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /federation-members/:id} : Partial updates given fields of an existing federationMember, field will ignore if it is null
     *
     * @param id the id of the federationMemberDTO to save.
     * @param federationMemberDTO the federationMemberDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated federationMemberDTO,
     * or with status {@code 400 (Bad Request)} if the federationMemberDTO is not valid,
     * or with status {@code 404 (Not Found)} if the federationMemberDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the federationMemberDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/federation-members/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<FederationMemberDTO> partialUpdateFederationMember(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FederationMemberDTO federationMemberDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update FederationMember partially : {}, {}", id, federationMemberDTO);
        if (federationMemberDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, federationMemberDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!federationMemberRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FederationMemberDTO> result = federationMemberService.partialUpdate(federationMemberDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, federationMemberDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /federation-members} : get all the federationMembers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of federationMembers in body.
     */
    @GetMapping("/federation-members")
    public ResponseEntity<List<FederationMemberDTO>> getAllFederationMembers(FederationMemberCriteria criteria, Pageable pageable) {
        log.debug("REST request to get FederationMembers by criteria: {}", criteria);
        Page<FederationMemberDTO> page = federationMemberQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /federation-members/count} : count all the federationMembers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/federation-members/count")
    public ResponseEntity<Long> countFederationMembers(FederationMemberCriteria criteria) {
        log.debug("REST request to count FederationMembers by criteria: {}", criteria);
        return ResponseEntity.ok().body(federationMemberQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /federation-members/:id} : get the "id" federationMember.
     *
     * @param id the id of the federationMemberDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the federationMemberDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/federation-members/{id}")
    public ResponseEntity<FederationMemberDTO> getFederationMember(@PathVariable Long id) {
        log.debug("REST request to get FederationMember : {}", id);
        Optional<FederationMemberDTO> federationMemberDTO = federationMemberService.findOne(id);
        return ResponseUtil.wrapOrNotFound(federationMemberDTO);
    }

    /**
     * {@code DELETE  /federation-members/:id} : delete the "id" federationMember.
     *
     * @param id the id of the federationMemberDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/federation-members/{id}")
    public ResponseEntity<Void> deleteFederationMember(@PathVariable Long id) {
        log.debug("REST request to delete FederationMember : {}", id);
        federationMemberService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
