package com.github.agrimint.web.rest;

import com.github.agrimint.repository.InviteRepository;
import com.github.agrimint.service.InviteQueryService;
import com.github.agrimint.service.InviteService;
import com.github.agrimint.service.criteria.InviteCriteria;
import com.github.agrimint.service.dto.InviteDTO;
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
 * REST controller for managing {@link com.github.agrimint.domain.Invite}.
 */
@RestController
@RequestMapping("/api")
public class InviteResource {

    private final Logger log = LoggerFactory.getLogger(InviteResource.class);

    private static final String ENTITY_NAME = "agriMintInvite";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InviteService inviteService;

    private final InviteRepository inviteRepository;

    private final InviteQueryService inviteQueryService;

    public InviteResource(InviteService inviteService, InviteRepository inviteRepository, InviteQueryService inviteQueryService) {
        this.inviteService = inviteService;
        this.inviteRepository = inviteRepository;
        this.inviteQueryService = inviteQueryService;
    }

    /**
     * {@code POST  /invites} : Create a new invite.
     *
     * @param inviteDTO the inviteDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new inviteDTO, or with status {@code 400 (Bad Request)} if the invite has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/invites")
    public ResponseEntity<InviteDTO> createInvite(@Valid @RequestBody InviteDTO inviteDTO) throws URISyntaxException {
        log.debug("REST request to save Invite : {}", inviteDTO);
        if (inviteDTO.getId() != null) {
            throw new BadRequestAlertException("A new invite cannot already have an ID", ENTITY_NAME, "idexists");
        }
        InviteDTO result = inviteService.save(inviteDTO);
        return ResponseEntity
            .created(new URI("/api/invites/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /invites/:id} : Updates an existing invite.
     *
     * @param id the id of the inviteDTO to save.
     * @param inviteDTO the inviteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inviteDTO,
     * or with status {@code 400 (Bad Request)} if the inviteDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the inviteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/invites/{id}")
    public ResponseEntity<InviteDTO> updateInvite(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody InviteDTO inviteDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Invite : {}, {}", id, inviteDTO);
        if (inviteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inviteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inviteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        InviteDTO result = inviteService.save(inviteDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inviteDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /invites/:id} : Partial updates given fields of an existing invite, field will ignore if it is null
     *
     * @param id the id of the inviteDTO to save.
     * @param inviteDTO the inviteDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated inviteDTO,
     * or with status {@code 400 (Bad Request)} if the inviteDTO is not valid,
     * or with status {@code 404 (Not Found)} if the inviteDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the inviteDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/invites/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<InviteDTO> partialUpdateInvite(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody InviteDTO inviteDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Invite partially : {}, {}", id, inviteDTO);
        if (inviteDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, inviteDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!inviteRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<InviteDTO> result = inviteService.partialUpdate(inviteDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, inviteDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /invites} : get all the invites.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of invites in body.
     */
    @GetMapping("/invites")
    public ResponseEntity<List<InviteDTO>> getAllInvites(InviteCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Invites by criteria: {}", criteria);
        Page<InviteDTO> page = inviteQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /invites/count} : count all the invites.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/invites/count")
    public ResponseEntity<Long> countInvites(InviteCriteria criteria) {
        log.debug("REST request to count Invites by criteria: {}", criteria);
        return ResponseEntity.ok().body(inviteQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /invites/:id} : get the "id" invite.
     *
     * @param id the id of the inviteDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the inviteDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/invites/{id}")
    public ResponseEntity<InviteDTO> getInvite(@PathVariable Long id) {
        log.debug("REST request to get Invite : {}", id);
        Optional<InviteDTO> inviteDTO = inviteService.findOne(id);
        return ResponseUtil.wrapOrNotFound(inviteDTO);
    }

    /**
     * {@code DELETE  /invites/:id} : delete the "id" invite.
     *
     * @param id the id of the inviteDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/invites/{id}")
    public ResponseEntity<Void> deleteInvite(@PathVariable Long id) {
        log.debug("REST request to delete Invite : {}", id);
        inviteService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
