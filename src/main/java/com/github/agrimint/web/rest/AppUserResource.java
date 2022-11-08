package com.github.agrimint.web.rest;

import com.github.agrimint.repository.AppUserRepository;
import com.github.agrimint.service.AppUserQueryService;
import com.github.agrimint.service.AppUserService;
import com.github.agrimint.service.criteria.AppUserCriteria;
import com.github.agrimint.service.dto.AppUserDTO;
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
 * REST controller for managing {@link com.github.agrimint.domain.AppUser}.
 */
@RestController
@RequestMapping("/api")
public class AppUserResource {

    private final Logger log = LoggerFactory.getLogger(AppUserResource.class);

    private static final String ENTITY_NAME = "agriMintAppUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AppUserService appUserService;

    private final AppUserRepository appUserRepository;

    private final AppUserQueryService appUserQueryService;

    public AppUserResource(AppUserService appUserService, AppUserRepository appUserRepository, AppUserQueryService appUserQueryService) {
        this.appUserService = appUserService;
        this.appUserRepository = appUserRepository;
        this.appUserQueryService = appUserQueryService;
    }

    /**
     * {@code POST  /app-users} : Create a new appUser.
     *
     * @param appUserDTO the appUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new appUserDTO, or with status {@code 400 (Bad Request)} if the appUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/app-users")
    public ResponseEntity<AppUserDTO> createAppUser(@Valid @RequestBody AppUserDTO appUserDTO) throws URISyntaxException {
        log.debug("REST request to save AppUser : {}", appUserDTO);
        if (appUserDTO.getId() != null) {
            throw new BadRequestAlertException("A new appUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AppUserDTO result = appUserService.save(appUserDTO);
        return ResponseEntity
            .created(new URI("/api/app-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /app-users/:id} : Updates an existing appUser.
     *
     * @param id the id of the appUserDTO to save.
     * @param appUserDTO the appUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appUserDTO,
     * or with status {@code 400 (Bad Request)} if the appUserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the appUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/app-users/{id}")
    public ResponseEntity<AppUserDTO> updateAppUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AppUserDTO appUserDTO
    ) throws URISyntaxException {
        log.debug("REST request to update AppUser : {}, {}", id, appUserDTO);
        if (appUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AppUserDTO result = appUserService.save(appUserDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appUserDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /app-users/:id} : Partial updates given fields of an existing appUser, field will ignore if it is null
     *
     * @param id the id of the appUserDTO to save.
     * @param appUserDTO the appUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated appUserDTO,
     * or with status {@code 400 (Bad Request)} if the appUserDTO is not valid,
     * or with status {@code 404 (Not Found)} if the appUserDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the appUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/app-users/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<AppUserDTO> partialUpdateAppUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AppUserDTO appUserDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update AppUser partially : {}, {}", id, appUserDTO);
        if (appUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, appUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!appUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AppUserDTO> result = appUserService.partialUpdate(appUserDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, appUserDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /app-users} : get all the appUsers.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of appUsers in body.
     */
    @GetMapping("/app-users")
    public ResponseEntity<List<AppUserDTO>> getAllAppUsers(AppUserCriteria criteria, Pageable pageable) {
        log.debug("REST request to get AppUsers by criteria: {}", criteria);
        Page<AppUserDTO> page = appUserQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /app-users/count} : count all the appUsers.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/app-users/count")
    public ResponseEntity<Long> countAppUsers(AppUserCriteria criteria) {
        log.debug("REST request to count AppUsers by criteria: {}", criteria);
        return ResponseEntity.ok().body(appUserQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /app-users/:id} : get the "id" appUser.
     *
     * @param id the id of the appUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the appUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/app-users/{id}")
    public ResponseEntity<AppUserDTO> getAppUser(@PathVariable Long id) {
        log.debug("REST request to get AppUser : {}", id);
        Optional<AppUserDTO> appUserDTO = appUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(appUserDTO);
    }

    /**
     * {@code DELETE  /app-users/:id} : delete the "id" appUser.
     *
     * @param id the id of the appUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/app-users/{id}")
    public ResponseEntity<Void> deleteAppUser(@PathVariable Long id) {
        log.debug("REST request to delete AppUser : {}", id);
        appUserService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
