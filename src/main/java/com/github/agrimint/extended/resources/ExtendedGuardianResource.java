package com.github.agrimint.extended.resources;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
import com.github.agrimint.extended.exception.FederationExecption;
import com.github.agrimint.extended.exception.MemberAlreadyExistExecption;
import com.github.agrimint.extended.service.ExtendedGuardianService;
import com.github.agrimint.service.dto.MemberDTO;
import com.github.agrimint.web.rest.GuardianResource;
import java.net.URI;
import java.net.URISyntaxException;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link com.github.agrimint.domain.Guardian}.
 */
@RestController
@RequestMapping("/api/v1")
public class ExtendedGuardianResource {

    private static final String ENTITY_NAME = "agriMintGuardian";
    private final Logger log = LoggerFactory.getLogger(GuardianResource.class);
    private final ExtendedGuardianService guardianService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    public ExtendedGuardianResource(ExtendedGuardianService guardianService) {
        this.guardianService = guardianService;
    }

    /**
     * {@code POST  /guardians} : Create a new guardian.
     *
     * @param guardianDTO the guardianDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new guardianDTO, or with status {@code 400 (Bad Request)} if the guardian has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/guardian")
    public ResponseEntity<?> createGuardian(@Valid @RequestBody CreatMemberRequestDTO guardianDTO)
        throws URISyntaxException, MemberAlreadyExistExecption, FederationExecption {
        log.debug("REST request to save Guardian : {}", guardianDTO);

        MemberDTO result = guardianService.create(guardianDTO);

        return ResponseEntity
            .created(new URI("/api/members/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
}
