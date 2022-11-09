package com.github.agrimint.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.agrimint.IntegrationTest;
import com.github.agrimint.domain.Federation;
import com.github.agrimint.repository.FederationRepository;
import com.github.agrimint.service.criteria.FederationCriteria;
import com.github.agrimint.service.dto.FederationDTO;
import com.github.agrimint.service.mapper.FederationMapper;
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
 * Integration tests for the {@link FederationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FederationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_FEDIMINT_ID = "AAAAAAAAAA";
    private static final String UPDATED_FEDIMINT_ID = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMBER_OF_NODE = 1;
    private static final Integer UPDATED_NUMBER_OF_NODE = 2;
    private static final Integer SMALLER_NUMBER_OF_NODE = 1 - 1;

    private static final Long DEFAULT_BASE_PORT = 1L;
    private static final Long UPDATED_BASE_PORT = 2L;
    private static final Long SMALLER_BASE_PORT = 1L - 1L;

    private static final Integer DEFAULT_NUMBER_OF_REGISTERED_NODE = 1;
    private static final Integer UPDATED_NUMBER_OF_REGISTERED_NODE = 2;
    private static final Integer SMALLER_NUMBER_OF_REGISTERED_NODE = 1 - 1;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_DATE_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/federations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FederationRepository federationRepository;

    @Autowired
    private FederationMapper federationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFederationMockMvc;

    private Federation federation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Federation createEntity(EntityManager em) {
        Federation federation = new Federation()
            .name(DEFAULT_NAME)
            .fedimintId(DEFAULT_FEDIMINT_ID)
            .numberOfNode(DEFAULT_NUMBER_OF_NODE)
            .basePort(DEFAULT_BASE_PORT)
            .numberOfRegisteredNode(DEFAULT_NUMBER_OF_REGISTERED_NODE)
            .active(DEFAULT_ACTIVE)
            .dateCreated(DEFAULT_DATE_CREATED);
        return federation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Federation createUpdatedEntity(EntityManager em) {
        Federation federation = new Federation()
            .name(UPDATED_NAME)
            .fedimintId(UPDATED_FEDIMINT_ID)
            .numberOfNode(UPDATED_NUMBER_OF_NODE)
            .basePort(UPDATED_BASE_PORT)
            .numberOfRegisteredNode(UPDATED_NUMBER_OF_REGISTERED_NODE)
            .active(UPDATED_ACTIVE)
            .dateCreated(UPDATED_DATE_CREATED);
        return federation;
    }

    @BeforeEach
    public void initTest() {
        federation = createEntity(em);
    }

    @Test
    @Transactional
    void createFederation() throws Exception {
        int databaseSizeBeforeCreate = federationRepository.findAll().size();
        // Create the Federation
        FederationDTO federationDTO = federationMapper.toDto(federation);
        restFederationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationDTO)))
            .andExpect(status().isCreated());

        // Validate the Federation in the database
        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeCreate + 1);
        Federation testFederation = federationList.get(federationList.size() - 1);
        assertThat(testFederation.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFederation.getFedimintId()).isEqualTo(DEFAULT_FEDIMINT_ID);
        assertThat(testFederation.getNumberOfNode()).isEqualTo(DEFAULT_NUMBER_OF_NODE);
        assertThat(testFederation.getBasePort()).isEqualTo(DEFAULT_BASE_PORT);
        assertThat(testFederation.getNumberOfRegisteredNode()).isEqualTo(DEFAULT_NUMBER_OF_REGISTERED_NODE);
        assertThat(testFederation.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testFederation.getDateCreated()).isEqualTo(DEFAULT_DATE_CREATED);
    }

    @Test
    @Transactional
    void createFederationWithExistingId() throws Exception {
        // Create the Federation with an existing ID
        federation.setId(1L);
        FederationDTO federationDTO = federationMapper.toDto(federation);

        int databaseSizeBeforeCreate = federationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFederationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Federation in the database
        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = federationRepository.findAll().size();
        // set the field null
        federation.setName(null);

        // Create the Federation, which fails.
        FederationDTO federationDTO = federationMapper.toDto(federation);

        restFederationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationDTO)))
            .andExpect(status().isBadRequest());

        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNumberOfNodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = federationRepository.findAll().size();
        // set the field null
        federation.setNumberOfNode(null);

        // Create the Federation, which fails.
        FederationDTO federationDTO = federationMapper.toDto(federation);

        restFederationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationDTO)))
            .andExpect(status().isBadRequest());

        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNumberOfRegisteredNodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = federationRepository.findAll().size();
        // set the field null
        federation.setNumberOfRegisteredNode(null);

        // Create the Federation, which fails.
        FederationDTO federationDTO = federationMapper.toDto(federation);

        restFederationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationDTO)))
            .andExpect(status().isBadRequest());

        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = federationRepository.findAll().size();
        // set the field null
        federation.setActive(null);

        // Create the Federation, which fails.
        FederationDTO federationDTO = federationMapper.toDto(federation);

        restFederationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationDTO)))
            .andExpect(status().isBadRequest());

        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = federationRepository.findAll().size();
        // set the field null
        federation.setDateCreated(null);

        // Create the Federation, which fails.
        FederationDTO federationDTO = federationMapper.toDto(federation);

        restFederationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationDTO)))
            .andExpect(status().isBadRequest());

        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFederations() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList
        restFederationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(federation.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].fedimintId").value(hasItem(DEFAULT_FEDIMINT_ID)))
            .andExpect(jsonPath("$.[*].numberOfNode").value(hasItem(DEFAULT_NUMBER_OF_NODE)))
            .andExpect(jsonPath("$.[*].basePort").value(hasItem(DEFAULT_BASE_PORT.intValue())))
            .andExpect(jsonPath("$.[*].numberOfRegisteredNode").value(hasItem(DEFAULT_NUMBER_OF_REGISTERED_NODE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())));
    }

    @Test
    @Transactional
    void getFederation() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get the federation
        restFederationMockMvc
            .perform(get(ENTITY_API_URL_ID, federation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(federation.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.fedimintId").value(DEFAULT_FEDIMINT_ID))
            .andExpect(jsonPath("$.numberOfNode").value(DEFAULT_NUMBER_OF_NODE))
            .andExpect(jsonPath("$.basePort").value(DEFAULT_BASE_PORT.intValue()))
            .andExpect(jsonPath("$.numberOfRegisteredNode").value(DEFAULT_NUMBER_OF_REGISTERED_NODE))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.dateCreated").value(DEFAULT_DATE_CREATED.toString()));
    }

    @Test
    @Transactional
    void getFederationsByIdFiltering() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        Long id = federation.getId();

        defaultFederationShouldBeFound("id.equals=" + id);
        defaultFederationShouldNotBeFound("id.notEquals=" + id);

        defaultFederationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFederationShouldNotBeFound("id.greaterThan=" + id);

        defaultFederationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFederationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFederationsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where name equals to DEFAULT_NAME
        defaultFederationShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the federationList where name equals to UPDATED_NAME
        defaultFederationShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFederationsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where name not equals to DEFAULT_NAME
        defaultFederationShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the federationList where name not equals to UPDATED_NAME
        defaultFederationShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFederationsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where name in DEFAULT_NAME or UPDATED_NAME
        defaultFederationShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the federationList where name equals to UPDATED_NAME
        defaultFederationShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFederationsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where name is not null
        defaultFederationShouldBeFound("name.specified=true");

        // Get all the federationList where name is null
        defaultFederationShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllFederationsByNameContainsSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where name contains DEFAULT_NAME
        defaultFederationShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the federationList where name contains UPDATED_NAME
        defaultFederationShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFederationsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where name does not contain DEFAULT_NAME
        defaultFederationShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the federationList where name does not contain UPDATED_NAME
        defaultFederationShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFederationsByFedimintIdIsEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where fedimintId equals to DEFAULT_FEDIMINT_ID
        defaultFederationShouldBeFound("fedimintId.equals=" + DEFAULT_FEDIMINT_ID);

        // Get all the federationList where fedimintId equals to UPDATED_FEDIMINT_ID
        defaultFederationShouldNotBeFound("fedimintId.equals=" + UPDATED_FEDIMINT_ID);
    }

    @Test
    @Transactional
    void getAllFederationsByFedimintIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where fedimintId not equals to DEFAULT_FEDIMINT_ID
        defaultFederationShouldNotBeFound("fedimintId.notEquals=" + DEFAULT_FEDIMINT_ID);

        // Get all the federationList where fedimintId not equals to UPDATED_FEDIMINT_ID
        defaultFederationShouldBeFound("fedimintId.notEquals=" + UPDATED_FEDIMINT_ID);
    }

    @Test
    @Transactional
    void getAllFederationsByFedimintIdIsInShouldWork() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where fedimintId in DEFAULT_FEDIMINT_ID or UPDATED_FEDIMINT_ID
        defaultFederationShouldBeFound("fedimintId.in=" + DEFAULT_FEDIMINT_ID + "," + UPDATED_FEDIMINT_ID);

        // Get all the federationList where fedimintId equals to UPDATED_FEDIMINT_ID
        defaultFederationShouldNotBeFound("fedimintId.in=" + UPDATED_FEDIMINT_ID);
    }

    @Test
    @Transactional
    void getAllFederationsByFedimintIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where fedimintId is not null
        defaultFederationShouldBeFound("fedimintId.specified=true");

        // Get all the federationList where fedimintId is null
        defaultFederationShouldNotBeFound("fedimintId.specified=false");
    }

    @Test
    @Transactional
    void getAllFederationsByFedimintIdContainsSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where fedimintId contains DEFAULT_FEDIMINT_ID
        defaultFederationShouldBeFound("fedimintId.contains=" + DEFAULT_FEDIMINT_ID);

        // Get all the federationList where fedimintId contains UPDATED_FEDIMINT_ID
        defaultFederationShouldNotBeFound("fedimintId.contains=" + UPDATED_FEDIMINT_ID);
    }

    @Test
    @Transactional
    void getAllFederationsByFedimintIdNotContainsSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where fedimintId does not contain DEFAULT_FEDIMINT_ID
        defaultFederationShouldNotBeFound("fedimintId.doesNotContain=" + DEFAULT_FEDIMINT_ID);

        // Get all the federationList where fedimintId does not contain UPDATED_FEDIMINT_ID
        defaultFederationShouldBeFound("fedimintId.doesNotContain=" + UPDATED_FEDIMINT_ID);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfNodeIsEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfNode equals to DEFAULT_NUMBER_OF_NODE
        defaultFederationShouldBeFound("numberOfNode.equals=" + DEFAULT_NUMBER_OF_NODE);

        // Get all the federationList where numberOfNode equals to UPDATED_NUMBER_OF_NODE
        defaultFederationShouldNotBeFound("numberOfNode.equals=" + UPDATED_NUMBER_OF_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfNodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfNode not equals to DEFAULT_NUMBER_OF_NODE
        defaultFederationShouldNotBeFound("numberOfNode.notEquals=" + DEFAULT_NUMBER_OF_NODE);

        // Get all the federationList where numberOfNode not equals to UPDATED_NUMBER_OF_NODE
        defaultFederationShouldBeFound("numberOfNode.notEquals=" + UPDATED_NUMBER_OF_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfNodeIsInShouldWork() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfNode in DEFAULT_NUMBER_OF_NODE or UPDATED_NUMBER_OF_NODE
        defaultFederationShouldBeFound("numberOfNode.in=" + DEFAULT_NUMBER_OF_NODE + "," + UPDATED_NUMBER_OF_NODE);

        // Get all the federationList where numberOfNode equals to UPDATED_NUMBER_OF_NODE
        defaultFederationShouldNotBeFound("numberOfNode.in=" + UPDATED_NUMBER_OF_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfNodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfNode is not null
        defaultFederationShouldBeFound("numberOfNode.specified=true");

        // Get all the federationList where numberOfNode is null
        defaultFederationShouldNotBeFound("numberOfNode.specified=false");
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfNodeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfNode is greater than or equal to DEFAULT_NUMBER_OF_NODE
        defaultFederationShouldBeFound("numberOfNode.greaterThanOrEqual=" + DEFAULT_NUMBER_OF_NODE);

        // Get all the federationList where numberOfNode is greater than or equal to UPDATED_NUMBER_OF_NODE
        defaultFederationShouldNotBeFound("numberOfNode.greaterThanOrEqual=" + UPDATED_NUMBER_OF_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfNodeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfNode is less than or equal to DEFAULT_NUMBER_OF_NODE
        defaultFederationShouldBeFound("numberOfNode.lessThanOrEqual=" + DEFAULT_NUMBER_OF_NODE);

        // Get all the federationList where numberOfNode is less than or equal to SMALLER_NUMBER_OF_NODE
        defaultFederationShouldNotBeFound("numberOfNode.lessThanOrEqual=" + SMALLER_NUMBER_OF_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfNodeIsLessThanSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfNode is less than DEFAULT_NUMBER_OF_NODE
        defaultFederationShouldNotBeFound("numberOfNode.lessThan=" + DEFAULT_NUMBER_OF_NODE);

        // Get all the federationList where numberOfNode is less than UPDATED_NUMBER_OF_NODE
        defaultFederationShouldBeFound("numberOfNode.lessThan=" + UPDATED_NUMBER_OF_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfNodeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfNode is greater than DEFAULT_NUMBER_OF_NODE
        defaultFederationShouldNotBeFound("numberOfNode.greaterThan=" + DEFAULT_NUMBER_OF_NODE);

        // Get all the federationList where numberOfNode is greater than SMALLER_NUMBER_OF_NODE
        defaultFederationShouldBeFound("numberOfNode.greaterThan=" + SMALLER_NUMBER_OF_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByBasePortIsEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where basePort equals to DEFAULT_BASE_PORT
        defaultFederationShouldBeFound("basePort.equals=" + DEFAULT_BASE_PORT);

        // Get all the federationList where basePort equals to UPDATED_BASE_PORT
        defaultFederationShouldNotBeFound("basePort.equals=" + UPDATED_BASE_PORT);
    }

    @Test
    @Transactional
    void getAllFederationsByBasePortIsNotEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where basePort not equals to DEFAULT_BASE_PORT
        defaultFederationShouldNotBeFound("basePort.notEquals=" + DEFAULT_BASE_PORT);

        // Get all the federationList where basePort not equals to UPDATED_BASE_PORT
        defaultFederationShouldBeFound("basePort.notEquals=" + UPDATED_BASE_PORT);
    }

    @Test
    @Transactional
    void getAllFederationsByBasePortIsInShouldWork() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where basePort in DEFAULT_BASE_PORT or UPDATED_BASE_PORT
        defaultFederationShouldBeFound("basePort.in=" + DEFAULT_BASE_PORT + "," + UPDATED_BASE_PORT);

        // Get all the federationList where basePort equals to UPDATED_BASE_PORT
        defaultFederationShouldNotBeFound("basePort.in=" + UPDATED_BASE_PORT);
    }

    @Test
    @Transactional
    void getAllFederationsByBasePortIsNullOrNotNull() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where basePort is not null
        defaultFederationShouldBeFound("basePort.specified=true");

        // Get all the federationList where basePort is null
        defaultFederationShouldNotBeFound("basePort.specified=false");
    }

    @Test
    @Transactional
    void getAllFederationsByBasePortIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where basePort is greater than or equal to DEFAULT_BASE_PORT
        defaultFederationShouldBeFound("basePort.greaterThanOrEqual=" + DEFAULT_BASE_PORT);

        // Get all the federationList where basePort is greater than or equal to UPDATED_BASE_PORT
        defaultFederationShouldNotBeFound("basePort.greaterThanOrEqual=" + UPDATED_BASE_PORT);
    }

    @Test
    @Transactional
    void getAllFederationsByBasePortIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where basePort is less than or equal to DEFAULT_BASE_PORT
        defaultFederationShouldBeFound("basePort.lessThanOrEqual=" + DEFAULT_BASE_PORT);

        // Get all the federationList where basePort is less than or equal to SMALLER_BASE_PORT
        defaultFederationShouldNotBeFound("basePort.lessThanOrEqual=" + SMALLER_BASE_PORT);
    }

    @Test
    @Transactional
    void getAllFederationsByBasePortIsLessThanSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where basePort is less than DEFAULT_BASE_PORT
        defaultFederationShouldNotBeFound("basePort.lessThan=" + DEFAULT_BASE_PORT);

        // Get all the federationList where basePort is less than UPDATED_BASE_PORT
        defaultFederationShouldBeFound("basePort.lessThan=" + UPDATED_BASE_PORT);
    }

    @Test
    @Transactional
    void getAllFederationsByBasePortIsGreaterThanSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where basePort is greater than DEFAULT_BASE_PORT
        defaultFederationShouldNotBeFound("basePort.greaterThan=" + DEFAULT_BASE_PORT);

        // Get all the federationList where basePort is greater than SMALLER_BASE_PORT
        defaultFederationShouldBeFound("basePort.greaterThan=" + SMALLER_BASE_PORT);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfRegisteredNodeIsEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfRegisteredNode equals to DEFAULT_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldBeFound("numberOfRegisteredNode.equals=" + DEFAULT_NUMBER_OF_REGISTERED_NODE);

        // Get all the federationList where numberOfRegisteredNode equals to UPDATED_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldNotBeFound("numberOfRegisteredNode.equals=" + UPDATED_NUMBER_OF_REGISTERED_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfRegisteredNodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfRegisteredNode not equals to DEFAULT_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldNotBeFound("numberOfRegisteredNode.notEquals=" + DEFAULT_NUMBER_OF_REGISTERED_NODE);

        // Get all the federationList where numberOfRegisteredNode not equals to UPDATED_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldBeFound("numberOfRegisteredNode.notEquals=" + UPDATED_NUMBER_OF_REGISTERED_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfRegisteredNodeIsInShouldWork() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfRegisteredNode in DEFAULT_NUMBER_OF_REGISTERED_NODE or UPDATED_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldBeFound(
            "numberOfRegisteredNode.in=" + DEFAULT_NUMBER_OF_REGISTERED_NODE + "," + UPDATED_NUMBER_OF_REGISTERED_NODE
        );

        // Get all the federationList where numberOfRegisteredNode equals to UPDATED_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldNotBeFound("numberOfRegisteredNode.in=" + UPDATED_NUMBER_OF_REGISTERED_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfRegisteredNodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfRegisteredNode is not null
        defaultFederationShouldBeFound("numberOfRegisteredNode.specified=true");

        // Get all the federationList where numberOfRegisteredNode is null
        defaultFederationShouldNotBeFound("numberOfRegisteredNode.specified=false");
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfRegisteredNodeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfRegisteredNode is greater than or equal to DEFAULT_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldBeFound("numberOfRegisteredNode.greaterThanOrEqual=" + DEFAULT_NUMBER_OF_REGISTERED_NODE);

        // Get all the federationList where numberOfRegisteredNode is greater than or equal to UPDATED_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldNotBeFound("numberOfRegisteredNode.greaterThanOrEqual=" + UPDATED_NUMBER_OF_REGISTERED_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfRegisteredNodeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfRegisteredNode is less than or equal to DEFAULT_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldBeFound("numberOfRegisteredNode.lessThanOrEqual=" + DEFAULT_NUMBER_OF_REGISTERED_NODE);

        // Get all the federationList where numberOfRegisteredNode is less than or equal to SMALLER_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldNotBeFound("numberOfRegisteredNode.lessThanOrEqual=" + SMALLER_NUMBER_OF_REGISTERED_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfRegisteredNodeIsLessThanSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfRegisteredNode is less than DEFAULT_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldNotBeFound("numberOfRegisteredNode.lessThan=" + DEFAULT_NUMBER_OF_REGISTERED_NODE);

        // Get all the federationList where numberOfRegisteredNode is less than UPDATED_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldBeFound("numberOfRegisteredNode.lessThan=" + UPDATED_NUMBER_OF_REGISTERED_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByNumberOfRegisteredNodeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where numberOfRegisteredNode is greater than DEFAULT_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldNotBeFound("numberOfRegisteredNode.greaterThan=" + DEFAULT_NUMBER_OF_REGISTERED_NODE);

        // Get all the federationList where numberOfRegisteredNode is greater than SMALLER_NUMBER_OF_REGISTERED_NODE
        defaultFederationShouldBeFound("numberOfRegisteredNode.greaterThan=" + SMALLER_NUMBER_OF_REGISTERED_NODE);
    }

    @Test
    @Transactional
    void getAllFederationsByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where active equals to DEFAULT_ACTIVE
        defaultFederationShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the federationList where active equals to UPDATED_ACTIVE
        defaultFederationShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllFederationsByActiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where active not equals to DEFAULT_ACTIVE
        defaultFederationShouldNotBeFound("active.notEquals=" + DEFAULT_ACTIVE);

        // Get all the federationList where active not equals to UPDATED_ACTIVE
        defaultFederationShouldBeFound("active.notEquals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllFederationsByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultFederationShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the federationList where active equals to UPDATED_ACTIVE
        defaultFederationShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllFederationsByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where active is not null
        defaultFederationShouldBeFound("active.specified=true");

        // Get all the federationList where active is null
        defaultFederationShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllFederationsByDateCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where dateCreated equals to DEFAULT_DATE_CREATED
        defaultFederationShouldBeFound("dateCreated.equals=" + DEFAULT_DATE_CREATED);

        // Get all the federationList where dateCreated equals to UPDATED_DATE_CREATED
        defaultFederationShouldNotBeFound("dateCreated.equals=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllFederationsByDateCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where dateCreated not equals to DEFAULT_DATE_CREATED
        defaultFederationShouldNotBeFound("dateCreated.notEquals=" + DEFAULT_DATE_CREATED);

        // Get all the federationList where dateCreated not equals to UPDATED_DATE_CREATED
        defaultFederationShouldBeFound("dateCreated.notEquals=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllFederationsByDateCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where dateCreated in DEFAULT_DATE_CREATED or UPDATED_DATE_CREATED
        defaultFederationShouldBeFound("dateCreated.in=" + DEFAULT_DATE_CREATED + "," + UPDATED_DATE_CREATED);

        // Get all the federationList where dateCreated equals to UPDATED_DATE_CREATED
        defaultFederationShouldNotBeFound("dateCreated.in=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllFederationsByDateCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        // Get all the federationList where dateCreated is not null
        defaultFederationShouldBeFound("dateCreated.specified=true");

        // Get all the federationList where dateCreated is null
        defaultFederationShouldNotBeFound("dateCreated.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFederationShouldBeFound(String filter) throws Exception {
        restFederationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(federation.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].fedimintId").value(hasItem(DEFAULT_FEDIMINT_ID)))
            .andExpect(jsonPath("$.[*].numberOfNode").value(hasItem(DEFAULT_NUMBER_OF_NODE)))
            .andExpect(jsonPath("$.[*].basePort").value(hasItem(DEFAULT_BASE_PORT.intValue())))
            .andExpect(jsonPath("$.[*].numberOfRegisteredNode").value(hasItem(DEFAULT_NUMBER_OF_REGISTERED_NODE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())));

        // Check, that the count call also returns 1
        restFederationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFederationShouldNotBeFound(String filter) throws Exception {
        restFederationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFederationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFederation() throws Exception {
        // Get the federation
        restFederationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewFederation() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        int databaseSizeBeforeUpdate = federationRepository.findAll().size();

        // Update the federation
        Federation updatedFederation = federationRepository.findById(federation.getId()).get();
        // Disconnect from session so that the updates on updatedFederation are not directly saved in db
        em.detach(updatedFederation);
        updatedFederation
            .name(UPDATED_NAME)
            .fedimintId(UPDATED_FEDIMINT_ID)
            .numberOfNode(UPDATED_NUMBER_OF_NODE)
            .basePort(UPDATED_BASE_PORT)
            .numberOfRegisteredNode(UPDATED_NUMBER_OF_REGISTERED_NODE)
            .active(UPDATED_ACTIVE)
            .dateCreated(UPDATED_DATE_CREATED);
        FederationDTO federationDTO = federationMapper.toDto(updatedFederation);

        restFederationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, federationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(federationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Federation in the database
        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeUpdate);
        Federation testFederation = federationList.get(federationList.size() - 1);
        assertThat(testFederation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFederation.getFedimintId()).isEqualTo(UPDATED_FEDIMINT_ID);
        assertThat(testFederation.getNumberOfNode()).isEqualTo(UPDATED_NUMBER_OF_NODE);
        assertThat(testFederation.getBasePort()).isEqualTo(UPDATED_BASE_PORT);
        assertThat(testFederation.getNumberOfRegisteredNode()).isEqualTo(UPDATED_NUMBER_OF_REGISTERED_NODE);
        assertThat(testFederation.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testFederation.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void putNonExistingFederation() throws Exception {
        int databaseSizeBeforeUpdate = federationRepository.findAll().size();
        federation.setId(count.incrementAndGet());

        // Create the Federation
        FederationDTO federationDTO = federationMapper.toDto(federation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFederationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, federationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(federationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Federation in the database
        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFederation() throws Exception {
        int databaseSizeBeforeUpdate = federationRepository.findAll().size();
        federation.setId(count.incrementAndGet());

        // Create the Federation
        FederationDTO federationDTO = federationMapper.toDto(federation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFederationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(federationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Federation in the database
        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFederation() throws Exception {
        int databaseSizeBeforeUpdate = federationRepository.findAll().size();
        federation.setId(count.incrementAndGet());

        // Create the Federation
        FederationDTO federationDTO = federationMapper.toDto(federation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFederationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(federationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Federation in the database
        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFederationWithPatch() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        int databaseSizeBeforeUpdate = federationRepository.findAll().size();

        // Update the federation using partial update
        Federation partialUpdatedFederation = new Federation();
        partialUpdatedFederation.setId(federation.getId());

        partialUpdatedFederation
            .name(UPDATED_NAME)
            .numberOfNode(UPDATED_NUMBER_OF_NODE)
            .basePort(UPDATED_BASE_PORT)
            .dateCreated(UPDATED_DATE_CREATED);

        restFederationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFederation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFederation))
            )
            .andExpect(status().isOk());

        // Validate the Federation in the database
        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeUpdate);
        Federation testFederation = federationList.get(federationList.size() - 1);
        assertThat(testFederation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFederation.getFedimintId()).isEqualTo(DEFAULT_FEDIMINT_ID);
        assertThat(testFederation.getNumberOfNode()).isEqualTo(UPDATED_NUMBER_OF_NODE);
        assertThat(testFederation.getBasePort()).isEqualTo(UPDATED_BASE_PORT);
        assertThat(testFederation.getNumberOfRegisteredNode()).isEqualTo(DEFAULT_NUMBER_OF_REGISTERED_NODE);
        assertThat(testFederation.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testFederation.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void fullUpdateFederationWithPatch() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        int databaseSizeBeforeUpdate = federationRepository.findAll().size();

        // Update the federation using partial update
        Federation partialUpdatedFederation = new Federation();
        partialUpdatedFederation.setId(federation.getId());

        partialUpdatedFederation
            .name(UPDATED_NAME)
            .fedimintId(UPDATED_FEDIMINT_ID)
            .numberOfNode(UPDATED_NUMBER_OF_NODE)
            .basePort(UPDATED_BASE_PORT)
            .numberOfRegisteredNode(UPDATED_NUMBER_OF_REGISTERED_NODE)
            .active(UPDATED_ACTIVE)
            .dateCreated(UPDATED_DATE_CREATED);

        restFederationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFederation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFederation))
            )
            .andExpect(status().isOk());

        // Validate the Federation in the database
        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeUpdate);
        Federation testFederation = federationList.get(federationList.size() - 1);
        assertThat(testFederation.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFederation.getFedimintId()).isEqualTo(UPDATED_FEDIMINT_ID);
        assertThat(testFederation.getNumberOfNode()).isEqualTo(UPDATED_NUMBER_OF_NODE);
        assertThat(testFederation.getBasePort()).isEqualTo(UPDATED_BASE_PORT);
        assertThat(testFederation.getNumberOfRegisteredNode()).isEqualTo(UPDATED_NUMBER_OF_REGISTERED_NODE);
        assertThat(testFederation.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testFederation.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void patchNonExistingFederation() throws Exception {
        int databaseSizeBeforeUpdate = federationRepository.findAll().size();
        federation.setId(count.incrementAndGet());

        // Create the Federation
        FederationDTO federationDTO = federationMapper.toDto(federation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFederationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, federationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(federationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Federation in the database
        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFederation() throws Exception {
        int databaseSizeBeforeUpdate = federationRepository.findAll().size();
        federation.setId(count.incrementAndGet());

        // Create the Federation
        FederationDTO federationDTO = federationMapper.toDto(federation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFederationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(federationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Federation in the database
        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFederation() throws Exception {
        int databaseSizeBeforeUpdate = federationRepository.findAll().size();
        federation.setId(count.incrementAndGet());

        // Create the Federation
        FederationDTO federationDTO = federationMapper.toDto(federation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFederationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(federationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Federation in the database
        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFederation() throws Exception {
        // Initialize the database
        federationRepository.saveAndFlush(federation);

        int databaseSizeBeforeDelete = federationRepository.findAll().size();

        // Delete the federation
        restFederationMockMvc
            .perform(delete(ENTITY_API_URL_ID, federation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Federation> federationList = federationRepository.findAll();
        assertThat(federationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
