package com.github.agrimint.extended.resources;

import com.github.agrimint.extended.dto.CreatMemberRequestDTO;
import com.github.agrimint.extended.exception.MemberExecption;
import com.github.agrimint.extended.service.ExtendedMemberService;
import com.github.agrimint.extended.util.ApplicationUrl;
import com.github.agrimint.repository.MemberRepository;
import com.github.agrimint.service.MemberQueryService;
import com.github.agrimint.service.criteria.MemberCriteria;
import com.github.agrimint.service.dto.MemberDTO;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.validation.Valid;
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
 * REST controller for managing {@link com.github.agrimint.domain.Member}.
 */
@RestController
@RequestMapping(ApplicationUrl.BASE_CONTEXT_URL)
public class ExtendedMemberResource {

    private final Logger log = LoggerFactory.getLogger(ExtendedMemberResource.class);

    private static final String ENTITY_NAME = "agriMintMember";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExtendedMemberService memberService;

    private final MemberRepository memberRepository;

    private final MemberQueryService memberQueryService;

    public ExtendedMemberResource(
        ExtendedMemberService memberService,
        MemberRepository memberRepository,
        MemberQueryService memberQueryService
    ) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.memberQueryService = memberQueryService;
    }

    /**
     * {@code POST  /members} : Create a new member.
     *
     * @param memberDTO the memberDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new memberDTO, or with status {@code 400 (Bad Request)} if the member has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/member")
    public ResponseEntity<?> createMember(
        @Valid @RequestBody CreatMemberRequestDTO memberDTO,
        @RequestParam(value = "invitationCode", required = false) String invitationCode
    ) throws URISyntaxException, MemberExecption {
        log.debug("REST request to save Member : {}", memberDTO);

        MemberDTO result = memberService.create(memberDTO, true, false, true, invitationCode);
        return ResponseEntity
            .created(new URI("/api/members/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /members} : get all the members.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of members in body.
     */
    @GetMapping("/member/federation/{federationId}")
    public ResponseEntity<List<MemberDTO>> getAllMembers(@PathVariable Long federationId, Pageable pageable) {
        log.debug("REST request to get Members by criteria: {}", federationId);
        List<MemberDTO> memberDTOS = memberService.getAll(federationId, pageable);
        return ResponseEntity.ok().body(memberDTOS);
    }

    /**
     * {@code GET  /members/:id} : get the "id" member.
     *
     * @param id the id of the memberDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the memberDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/members/{id}")
    public ResponseEntity<MemberDTO> getMember(@PathVariable Long id) {
        log.debug("REST request to get Member : {}", id);
        Optional<MemberDTO> memberDTO = memberService.getOne(id);
        return ResponseUtil.wrapOrNotFound(memberDTO);
    }
}
