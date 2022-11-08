package com.github.agrimint.web.rest;

import com.github.agrimint.repository.RoleRepository;
import com.github.agrimint.service.RoleQueryService;
import com.github.agrimint.service.RoleService;
import com.github.agrimint.service.criteria.RoleCriteria;
import com.github.agrimint.service.dto.RoleDTO;
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
 * REST controller for managing {@link com.github.agrimint.domain.Role}.
 */
@RestController
@RequestMapping("/api")
public class RoleResource {

    private final Logger log = LoggerFactory.getLogger(RoleResource.class);

    private static final String ENTITY_NAME = "agriMintRole";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RoleService roleService;

    private final RoleRepository roleRepository;

    private final RoleQueryService roleQueryService;

    public RoleResource(RoleService roleService, RoleRepository roleRepository, RoleQueryService roleQueryService) {
        this.roleService = roleService;
        this.roleRepository = roleRepository;
        this.roleQueryService = roleQueryService;
    }

    /**
     * {@code POST  /roles} : Create a new role.
     *
     * @param roleDTO the roleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new roleDTO, or with status {@code 400 (Bad Request)} if the role has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/roles")
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) throws URISyntaxException {
        log.debug("REST request to save Role : {}", roleDTO);
        if (roleDTO.getId() != null) {
            throw new BadRequestAlertException("A new role cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RoleDTO result = roleService.save(roleDTO);
        return ResponseEntity
            .created(new URI("/api/roles/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /roles/:id} : Updates an existing role.
     *
     * @param id the id of the roleDTO to save.
     * @param roleDTO the roleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roleDTO,
     * or with status {@code 400 (Bad Request)} if the roleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the roleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/roles/{id}")
    public ResponseEntity<RoleDTO> updateRole(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RoleDTO roleDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Role : {}, {}", id, roleDTO);
        if (roleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RoleDTO result = roleService.save(roleDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, roleDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /roles/:id} : Partial updates given fields of an existing role, field will ignore if it is null
     *
     * @param id the id of the roleDTO to save.
     * @param roleDTO the roleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roleDTO,
     * or with status {@code 400 (Bad Request)} if the roleDTO is not valid,
     * or with status {@code 404 (Not Found)} if the roleDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the roleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/roles/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<RoleDTO> partialUpdateRole(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RoleDTO roleDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Role partially : {}, {}", id, roleDTO);
        if (roleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roleDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!roleRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RoleDTO> result = roleService.partialUpdate(roleDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, roleDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /roles} : get all the roles.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of roles in body.
     */
    @GetMapping("/roles")
    public ResponseEntity<List<RoleDTO>> getAllRoles(RoleCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Roles by criteria: {}", criteria);
        Page<RoleDTO> page = roleQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /roles/count} : count all the roles.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/roles/count")
    public ResponseEntity<Long> countRoles(RoleCriteria criteria) {
        log.debug("REST request to count Roles by criteria: {}", criteria);
        return ResponseEntity.ok().body(roleQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /roles/:id} : get the "id" role.
     *
     * @param id the id of the roleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the roleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/roles/{id}")
    public ResponseEntity<RoleDTO> getRole(@PathVariable Long id) {
        log.debug("REST request to get Role : {}", id);
        Optional<RoleDTO> roleDTO = roleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(roleDTO);
    }

    /**
     * {@code DELETE  /roles/:id} : delete the "id" role.
     *
     * @param id the id of the roleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        log.debug("REST request to delete Role : {}", id);
        roleService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
