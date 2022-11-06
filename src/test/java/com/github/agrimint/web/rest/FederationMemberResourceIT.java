package com.github.agrimint.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.agrimint.IntegrationTest;
import com.github.agrimint.domain.FederationMember;
import com.github.agrimint.repository.FederationMemberRepository;
import com.github.agrimint.service.criteria.FederationMemberCriteria;
import com.github.agrimint.service.dto.FederationMemberDTO;
import com.github.agrimint.service.mapper.FederationMemberMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FederationMemberResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FederationMemberResourceIT {

    private static final Long DEFAULT_FEDERATION_ID = 1L;
    private static final Long UPDATED_FEDERATION_ID = 2L;
    private static final Long SMALLER_FEDERATION_ID = 1L - 1L;

    private static final Long DEFAULT_MEMBER_ID = 1L;
    private static final Long UPDATED_MEMBER_ID = 2L;
    private static final Long SMALLER_MEMBER_ID = 1L - 1L;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_DATE_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/federation-members";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FederationMemberRepository federationMemberRepository;

    @Autowired
    private FederationMemberMapper federationMemberMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFederationMemberMockMvc;

    private FederationMember federationMember;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FederationMember createEntity(EntityManager em) {
        FederationMember federationMember = new FederationMember()
            .federationId(DEFAULT_FEDERATION_ID)
            .memberId(DEFAULT_MEMBER_ID)
            .active(DEFAULT_ACTIVE)
            .dateCreated(DEFAULT_DATE_CREATED);
        return federationMember;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FederationMember createUpdatedEntity(EntityManager em) {
        FederationMember federationMember = new FederationMember()
            .federationId(UPDATED_FEDERATION_ID)
            .memberId(UPDATED_MEMBER_ID)
            .active(UPDATED_ACTIVE)
            .dateCreated(UPDATED_DATE_CREATED);
        return federationMember;
    }

    @BeforeEach
    public void initTest() {
        federationMember = createEntity(em);
    }

    @Test
    @Transactional
    void createFederationMember() throws Exception {
        int databaseSizeBeforeCreate = federationMemberRepository.findAll().size();
        // Create the FederationMember
        FederationMemberDTO federationMemberDTO = federationMemberMapper.toDto(federationMember);
        restFederationMemberMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationMemberDTO))
            )
            .andExpect(status().isCreated());

        // Validate the FederationMember in the database
        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeCreate + 1);
        FederationMember testFederationMember = federationMemberList.get(federationMemberList.size() - 1);
        assertThat(testFederationMember.getFederationId()).isEqualTo(DEFAULT_FEDERATION_ID);
        assertThat(testFederationMember.getMemberId()).isEqualTo(DEFAULT_MEMBER_ID);
        assertThat(testFederationMember.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testFederationMember.getDateCreated()).isEqualTo(DEFAULT_DATE_CREATED);
    }

    @Test
    @Transactional
    void createFederationMemberWithExistingId() throws Exception {
        // Create the FederationMember with an existing ID
        federationMember.setId(1L);
        FederationMemberDTO federationMemberDTO = federationMemberMapper.toDto(federationMember);

        int databaseSizeBeforeCreate = federationMemberRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFederationMemberMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FederationMember in the database
        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFederationIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = federationMemberRepository.findAll().size();
        // set the field null
        federationMember.setFederationId(null);

        // Create the FederationMember, which fails.
        FederationMemberDTO federationMemberDTO = federationMemberMapper.toDto(federationMember);

        restFederationMemberMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationMemberDTO))
            )
            .andExpect(status().isBadRequest());

        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMemberIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = federationMemberRepository.findAll().size();
        // set the field null
        federationMember.setMemberId(null);

        // Create the FederationMember, which fails.
        FederationMemberDTO federationMemberDTO = federationMemberMapper.toDto(federationMember);

        restFederationMemberMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationMemberDTO))
            )
            .andExpect(status().isBadRequest());

        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = federationMemberRepository.findAll().size();
        // set the field null
        federationMember.setActive(null);

        // Create the FederationMember, which fails.
        FederationMemberDTO federationMemberDTO = federationMemberMapper.toDto(federationMember);

        restFederationMemberMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationMemberDTO))
            )
            .andExpect(status().isBadRequest());

        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = federationMemberRepository.findAll().size();
        // set the field null
        federationMember.setDateCreated(null);

        // Create the FederationMember, which fails.
        FederationMemberDTO federationMemberDTO = federationMemberMapper.toDto(federationMember);

        restFederationMemberMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationMemberDTO))
            )
            .andExpect(status().isBadRequest());

        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFederationMembers() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList
        restFederationMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(federationMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].federationId").value(hasItem(DEFAULT_FEDERATION_ID.intValue())))
            .andExpect(jsonPath("$.[*].memberId").value(hasItem(DEFAULT_MEMBER_ID.intValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())));
    }

    @Test
    @Transactional
    void getFederationMember() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get the federationMember
        restFederationMemberMockMvc
            .perform(get(ENTITY_API_URL_ID, federationMember.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(federationMember.getId().intValue()))
            .andExpect(jsonPath("$.federationId").value(DEFAULT_FEDERATION_ID.intValue()))
            .andExpect(jsonPath("$.memberId").value(DEFAULT_MEMBER_ID.intValue()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.dateCreated").value(DEFAULT_DATE_CREATED.toString()));
    }

    @Test
    @Transactional
    void getFederationMembersByIdFiltering() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        Long id = federationMember.getId();

        defaultFederationMemberShouldBeFound("id.equals=" + id);
        defaultFederationMemberShouldNotBeFound("id.notEquals=" + id);

        defaultFederationMemberShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFederationMemberShouldNotBeFound("id.greaterThan=" + id);

        defaultFederationMemberShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFederationMemberShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFederationMembersByFederationIdIsEqualToSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where federationId equals to DEFAULT_FEDERATION_ID
        defaultFederationMemberShouldBeFound("federationId.equals=" + DEFAULT_FEDERATION_ID);

        // Get all the federationMemberList where federationId equals to UPDATED_FEDERATION_ID
        defaultFederationMemberShouldNotBeFound("federationId.equals=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByFederationIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where federationId not equals to DEFAULT_FEDERATION_ID
        defaultFederationMemberShouldNotBeFound("federationId.notEquals=" + DEFAULT_FEDERATION_ID);

        // Get all the federationMemberList where federationId not equals to UPDATED_FEDERATION_ID
        defaultFederationMemberShouldBeFound("federationId.notEquals=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByFederationIdIsInShouldWork() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where federationId in DEFAULT_FEDERATION_ID or UPDATED_FEDERATION_ID
        defaultFederationMemberShouldBeFound("federationId.in=" + DEFAULT_FEDERATION_ID + "," + UPDATED_FEDERATION_ID);

        // Get all the federationMemberList where federationId equals to UPDATED_FEDERATION_ID
        defaultFederationMemberShouldNotBeFound("federationId.in=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByFederationIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where federationId is not null
        defaultFederationMemberShouldBeFound("federationId.specified=true");

        // Get all the federationMemberList where federationId is null
        defaultFederationMemberShouldNotBeFound("federationId.specified=false");
    }

    @Test
    @Transactional
    void getAllFederationMembersByFederationIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where federationId is greater than or equal to DEFAULT_FEDERATION_ID
        defaultFederationMemberShouldBeFound("federationId.greaterThanOrEqual=" + DEFAULT_FEDERATION_ID);

        // Get all the federationMemberList where federationId is greater than or equal to UPDATED_FEDERATION_ID
        defaultFederationMemberShouldNotBeFound("federationId.greaterThanOrEqual=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByFederationIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where federationId is less than or equal to DEFAULT_FEDERATION_ID
        defaultFederationMemberShouldBeFound("federationId.lessThanOrEqual=" + DEFAULT_FEDERATION_ID);

        // Get all the federationMemberList where federationId is less than or equal to SMALLER_FEDERATION_ID
        defaultFederationMemberShouldNotBeFound("federationId.lessThanOrEqual=" + SMALLER_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByFederationIdIsLessThanSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where federationId is less than DEFAULT_FEDERATION_ID
        defaultFederationMemberShouldNotBeFound("federationId.lessThan=" + DEFAULT_FEDERATION_ID);

        // Get all the federationMemberList where federationId is less than UPDATED_FEDERATION_ID
        defaultFederationMemberShouldBeFound("federationId.lessThan=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByFederationIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where federationId is greater than DEFAULT_FEDERATION_ID
        defaultFederationMemberShouldNotBeFound("federationId.greaterThan=" + DEFAULT_FEDERATION_ID);

        // Get all the federationMemberList where federationId is greater than SMALLER_FEDERATION_ID
        defaultFederationMemberShouldBeFound("federationId.greaterThan=" + SMALLER_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByMemberIdIsEqualToSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where memberId equals to DEFAULT_MEMBER_ID
        defaultFederationMemberShouldBeFound("memberId.equals=" + DEFAULT_MEMBER_ID);

        // Get all the federationMemberList where memberId equals to UPDATED_MEMBER_ID
        defaultFederationMemberShouldNotBeFound("memberId.equals=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByMemberIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where memberId not equals to DEFAULT_MEMBER_ID
        defaultFederationMemberShouldNotBeFound("memberId.notEquals=" + DEFAULT_MEMBER_ID);

        // Get all the federationMemberList where memberId not equals to UPDATED_MEMBER_ID
        defaultFederationMemberShouldBeFound("memberId.notEquals=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByMemberIdIsInShouldWork() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where memberId in DEFAULT_MEMBER_ID or UPDATED_MEMBER_ID
        defaultFederationMemberShouldBeFound("memberId.in=" + DEFAULT_MEMBER_ID + "," + UPDATED_MEMBER_ID);

        // Get all the federationMemberList where memberId equals to UPDATED_MEMBER_ID
        defaultFederationMemberShouldNotBeFound("memberId.in=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByMemberIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where memberId is not null
        defaultFederationMemberShouldBeFound("memberId.specified=true");

        // Get all the federationMemberList where memberId is null
        defaultFederationMemberShouldNotBeFound("memberId.specified=false");
    }

    @Test
    @Transactional
    void getAllFederationMembersByMemberIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where memberId is greater than or equal to DEFAULT_MEMBER_ID
        defaultFederationMemberShouldBeFound("memberId.greaterThanOrEqual=" + DEFAULT_MEMBER_ID);

        // Get all the federationMemberList where memberId is greater than or equal to UPDATED_MEMBER_ID
        defaultFederationMemberShouldNotBeFound("memberId.greaterThanOrEqual=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByMemberIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where memberId is less than or equal to DEFAULT_MEMBER_ID
        defaultFederationMemberShouldBeFound("memberId.lessThanOrEqual=" + DEFAULT_MEMBER_ID);

        // Get all the federationMemberList where memberId is less than or equal to SMALLER_MEMBER_ID
        defaultFederationMemberShouldNotBeFound("memberId.lessThanOrEqual=" + SMALLER_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByMemberIdIsLessThanSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where memberId is less than DEFAULT_MEMBER_ID
        defaultFederationMemberShouldNotBeFound("memberId.lessThan=" + DEFAULT_MEMBER_ID);

        // Get all the federationMemberList where memberId is less than UPDATED_MEMBER_ID
        defaultFederationMemberShouldBeFound("memberId.lessThan=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByMemberIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where memberId is greater than DEFAULT_MEMBER_ID
        defaultFederationMemberShouldNotBeFound("memberId.greaterThan=" + DEFAULT_MEMBER_ID);

        // Get all the federationMemberList where memberId is greater than SMALLER_MEMBER_ID
        defaultFederationMemberShouldBeFound("memberId.greaterThan=" + SMALLER_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllFederationMembersByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where active equals to DEFAULT_ACTIVE
        defaultFederationMemberShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the federationMemberList where active equals to UPDATED_ACTIVE
        defaultFederationMemberShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllFederationMembersByActiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where active not equals to DEFAULT_ACTIVE
        defaultFederationMemberShouldNotBeFound("active.notEquals=" + DEFAULT_ACTIVE);

        // Get all the federationMemberList where active not equals to UPDATED_ACTIVE
        defaultFederationMemberShouldBeFound("active.notEquals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllFederationMembersByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultFederationMemberShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the federationMemberList where active equals to UPDATED_ACTIVE
        defaultFederationMemberShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllFederationMembersByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where active is not null
        defaultFederationMemberShouldBeFound("active.specified=true");

        // Get all the federationMemberList where active is null
        defaultFederationMemberShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllFederationMembersByDateCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where dateCreated equals to DEFAULT_DATE_CREATED
        defaultFederationMemberShouldBeFound("dateCreated.equals=" + DEFAULT_DATE_CREATED);

        // Get all the federationMemberList where dateCreated equals to UPDATED_DATE_CREATED
        defaultFederationMemberShouldNotBeFound("dateCreated.equals=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllFederationMembersByDateCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where dateCreated not equals to DEFAULT_DATE_CREATED
        defaultFederationMemberShouldNotBeFound("dateCreated.notEquals=" + DEFAULT_DATE_CREATED);

        // Get all the federationMemberList where dateCreated not equals to UPDATED_DATE_CREATED
        defaultFederationMemberShouldBeFound("dateCreated.notEquals=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllFederationMembersByDateCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where dateCreated in DEFAULT_DATE_CREATED or UPDATED_DATE_CREATED
        defaultFederationMemberShouldBeFound("dateCreated.in=" + DEFAULT_DATE_CREATED + "," + UPDATED_DATE_CREATED);

        // Get all the federationMemberList where dateCreated equals to UPDATED_DATE_CREATED
        defaultFederationMemberShouldNotBeFound("dateCreated.in=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllFederationMembersByDateCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        // Get all the federationMemberList where dateCreated is not null
        defaultFederationMemberShouldBeFound("dateCreated.specified=true");

        // Get all the federationMemberList where dateCreated is null
        defaultFederationMemberShouldNotBeFound("dateCreated.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFederationMemberShouldBeFound(String filter) throws Exception {
        restFederationMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(federationMember.getId().intValue())))
            .andExpect(jsonPath("$.[*].federationId").value(hasItem(DEFAULT_FEDERATION_ID.intValue())))
            .andExpect(jsonPath("$.[*].memberId").value(hasItem(DEFAULT_MEMBER_ID.intValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())));

        // Check, that the count call also returns 1
        restFederationMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFederationMemberShouldNotBeFound(String filter) throws Exception {
        restFederationMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFederationMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFederationMember() throws Exception {
        // Get the federationMember
        restFederationMemberMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFederationMember() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        int databaseSizeBeforeUpdate = federationMemberRepository.findAll().size();

        // Update the federationMember
        FederationMember updatedFederationMember = federationMemberRepository.findById(federationMember.getId()).get();
        // Disconnect from session so that the updates on updatedFederationMember are not directly saved in db
        em.detach(updatedFederationMember);
        updatedFederationMember
            .federationId(UPDATED_FEDERATION_ID)
            .memberId(UPDATED_MEMBER_ID)
            .active(UPDATED_ACTIVE)
            .dateCreated(UPDATED_DATE_CREATED);
        FederationMemberDTO federationMemberDTO = federationMemberMapper.toDto(updatedFederationMember);

        restFederationMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, federationMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(federationMemberDTO))
            )
            .andExpect(status().isOk());

        // Validate the FederationMember in the database
        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeUpdate);
        FederationMember testFederationMember = federationMemberList.get(federationMemberList.size() - 1);
        assertThat(testFederationMember.getFederationId()).isEqualTo(UPDATED_FEDERATION_ID);
        assertThat(testFederationMember.getMemberId()).isEqualTo(UPDATED_MEMBER_ID);
        assertThat(testFederationMember.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testFederationMember.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void putNonExistingFederationMember() throws Exception {
        int databaseSizeBeforeUpdate = federationMemberRepository.findAll().size();
        federationMember.setId(count.incrementAndGet());

        // Create the FederationMember
        FederationMemberDTO federationMemberDTO = federationMemberMapper.toDto(federationMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFederationMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, federationMemberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(federationMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FederationMember in the database
        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFederationMember() throws Exception {
        int databaseSizeBeforeUpdate = federationMemberRepository.findAll().size();
        federationMember.setId(count.incrementAndGet());

        // Create the FederationMember
        FederationMemberDTO federationMemberDTO = federationMemberMapper.toDto(federationMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFederationMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(federationMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FederationMember in the database
        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFederationMember() throws Exception {
        int databaseSizeBeforeUpdate = federationMemberRepository.findAll().size();
        federationMember.setId(count.incrementAndGet());

        // Create the FederationMember
        FederationMemberDTO federationMemberDTO = federationMemberMapper.toDto(federationMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFederationMemberMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationMemberDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FederationMember in the database
        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFederationMemberWithPatch() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        int databaseSizeBeforeUpdate = federationMemberRepository.findAll().size();

        // Update the federationMember using partial update
        FederationMember partialUpdatedFederationMember = new FederationMember();
        partialUpdatedFederationMember.setId(federationMember.getId());

        partialUpdatedFederationMember.federationId(UPDATED_FEDERATION_ID).active(UPDATED_ACTIVE);

        restFederationMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFederationMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFederationMember))
            )
            .andExpect(status().isOk());

        // Validate the FederationMember in the database
        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeUpdate);
        FederationMember testFederationMember = federationMemberList.get(federationMemberList.size() - 1);
        assertThat(testFederationMember.getFederationId()).isEqualTo(UPDATED_FEDERATION_ID);
        assertThat(testFederationMember.getMemberId()).isEqualTo(DEFAULT_MEMBER_ID);
        assertThat(testFederationMember.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testFederationMember.getDateCreated()).isEqualTo(DEFAULT_DATE_CREATED);
    }

    @Test
    @Transactional
    void fullUpdateFederationMemberWithPatch() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        int databaseSizeBeforeUpdate = federationMemberRepository.findAll().size();

        // Update the federationMember using partial update
        FederationMember partialUpdatedFederationMember = new FederationMember();
        partialUpdatedFederationMember.setId(federationMember.getId());

        partialUpdatedFederationMember
            .federationId(UPDATED_FEDERATION_ID)
            .memberId(UPDATED_MEMBER_ID)
            .active(UPDATED_ACTIVE)
            .dateCreated(UPDATED_DATE_CREATED);

        restFederationMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFederationMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFederationMember))
            )
            .andExpect(status().isOk());

        // Validate the FederationMember in the database
        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeUpdate);
        FederationMember testFederationMember = federationMemberList.get(federationMemberList.size() - 1);
        assertThat(testFederationMember.getFederationId()).isEqualTo(UPDATED_FEDERATION_ID);
        assertThat(testFederationMember.getMemberId()).isEqualTo(UPDATED_MEMBER_ID);
        assertThat(testFederationMember.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testFederationMember.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void patchNonExistingFederationMember() throws Exception {
        int databaseSizeBeforeUpdate = federationMemberRepository.findAll().size();
        federationMember.setId(count.incrementAndGet());

        // Create the FederationMember
        FederationMemberDTO federationMemberDTO = federationMemberMapper.toDto(federationMember);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFederationMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, federationMemberDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(federationMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FederationMember in the database
        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFederationMember() throws Exception {
        int databaseSizeBeforeUpdate = federationMemberRepository.findAll().size();
        federationMember.setId(count.incrementAndGet());

        // Create the FederationMember
        FederationMemberDTO federationMemberDTO = federationMemberMapper.toDto(federationMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFederationMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(federationMemberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FederationMember in the database
        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFederationMember() throws Exception {
        int databaseSizeBeforeUpdate = federationMemberRepository.findAll().size();
        federationMember.setId(count.incrementAndGet());

        // Create the FederationMember
        FederationMemberDTO federationMemberDTO = federationMemberMapper.toDto(federationMember);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFederationMemberMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(federationMemberDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FederationMember in the database
        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFederationMember() throws Exception {
        // Initialize the database
        federationMemberRepository.saveAndFlush(federationMember);

        int databaseSizeBeforeDelete = federationMemberRepository.findAll().size();

        // Delete the federationMember
        restFederationMemberMockMvc
            .perform(delete(ENTITY_API_URL_ID, federationMember.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FederationMember> federationMemberList = federationMemberRepository.findAll();
        assertThat(federationMemberList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
