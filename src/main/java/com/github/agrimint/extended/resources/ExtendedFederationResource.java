package com.github.agrimint.extended.resources;

import com.github.agrimint.extended.dto.CreateFederationRequestDTO;
import com.github.agrimint.extended.dto.GetConnectionFedimintHttpResponse;
import com.github.agrimint.extended.exeception.FederationExecption;
import com.github.agrimint.extended.service.ExtendedFederationService;
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
@RequestMapping("/api/v1")
public class ExtendedFederationResource {

    private final Logger log = LoggerFactory.getLogger(ExtendedFederationResource.class);

    private static final String ENTITY_NAME = "agriMintFederation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExtendedFederationService federationService;

    public ExtendedFederationResource(ExtendedFederationService federationService) {
        this.federationService = federationService;
    }

    /**
     * {@code POST  /federations} : Create a new federation.
     *
     * @param federationDTO the federationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new federationDTO, or with status {@code 400 (Bad Request)} if the federation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/federation")
    public ResponseEntity<FederationDTO> createFederation(@Valid @RequestBody CreateFederationRequestDTO federationDTO)
        throws URISyntaxException, FederationExecption {
        log.debug("REST request to save Federation : {}", federationDTO);

        FederationDTO result = federationService.create(federationDTO);
        return ResponseEntity
            .created(new URI("/api/federations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
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
        Page<FederationDTO> page = federationService.getAll(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/federations/{id}/connection")
    public ResponseEntity<GetConnectionFedimintHttpResponse> getFederationConnection(@PathVariable Long id) throws FederationExecption {
        log.debug("REST request to get Federation Connection : {}", id);
        GetConnectionFedimintHttpResponse federationConnection = federationService.getFederationConnection(id);
        return ResponseEntity.ok().body(federationConnection);
    }
}
