package com.github.agrimint.extended.resources;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
import com.github.agrimint.extended.exeception.FederationNotFoundExecption;
import com.github.agrimint.extended.exeception.MemberAlreadyExistExecption;
import com.github.agrimint.extended.service.ExtendedGuardianService;
import com.github.agrimint.repository.GuardianRepository;
import com.github.agrimint.service.GuardianQueryService;
import com.github.agrimint.service.GuardianService;
import com.github.agrimint.service.criteria.GuardianCriteria;
import com.github.agrimint.service.dto.GuardianDTO;
import com.github.agrimint.service.dto.MemberDTO;
import com.github.agrimint.web.rest.GuardianResource;
import com.github.agrimint.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
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
@RequestMapping("/api/v1")
public class ExtendedGuardianResource {

    private final Logger log = LoggerFactory.getLogger(GuardianResource.class);

    private static final String ENTITY_NAME = "agriMintGuardian";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExtendedGuardianService guardianService;

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
        throws URISyntaxException, MemberAlreadyExistExecption, FederationNotFoundExecption {
        log.debug("REST request to save Guardian : {}", guardianDTO);

        MemberDTO result = null;

        try {
            result = guardianService.create(guardianDTO);
        } catch (MemberAlreadyExistExecption | FederationNotFoundExecption ex) {
            Map<String, String> errorMessageMap = new HashMap<>();
            errorMessageMap.put("errorMessage", ex.getMessage());
            return ResponseEntity.badRequest().body(errorMessageMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity
            .created(new URI("/api/members/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }
}
