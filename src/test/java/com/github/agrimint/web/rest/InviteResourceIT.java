package com.github.agrimint.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.agrimint.IntegrationTest;
import com.github.agrimint.domain.Invite;
import com.github.agrimint.repository.InviteRepository;
import com.github.agrimint.service.criteria.InviteCriteria;
import com.github.agrimint.service.dto.InviteDTO;
import com.github.agrimint.service.mapper.InviteMapper;
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
 * Integration tests for the {@link InviteResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class InviteResourceIT {

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_INVITATION_CODE = "AAAAAAAAAA";
    private static final String UPDATED_INVITATION_CODE = "BBBBBBBBBB";

    private static final Long DEFAULT_FEDERATION_ID = 1L;
    private static final Long UPDATED_FEDERATION_ID = 2L;
    private static final Long SMALLER_FEDERATION_ID = 1L - 1L;

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/invites";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private InviteRepository inviteRepository;

    @Autowired
    private InviteMapper inviteMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInviteMockMvc;

    private Invite invite;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invite createEntity(EntityManager em) {
        Invite invite = new Invite()
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .countryCode(DEFAULT_COUNTRY_CODE)
            .invitationCode(DEFAULT_INVITATION_CODE)
            .federationId(DEFAULT_FEDERATION_ID)
            .active(DEFAULT_ACTIVE);
        return invite;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Invite createUpdatedEntity(EntityManager em) {
        Invite invite = new Invite()
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .countryCode(UPDATED_COUNTRY_CODE)
            .invitationCode(UPDATED_INVITATION_CODE)
            .federationId(UPDATED_FEDERATION_ID)
            .active(UPDATED_ACTIVE);
        return invite;
    }

    @BeforeEach
    public void initTest() {
        invite = createEntity(em);
    }

    @Test
    @Transactional
    void createInvite() throws Exception {
        int databaseSizeBeforeCreate = inviteRepository.findAll().size();
        // Create the Invite
        InviteDTO inviteDTO = inviteMapper.toDto(invite);
        restInviteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inviteDTO)))
            .andExpect(status().isCreated());

        // Validate the Invite in the database
        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeCreate + 1);
        Invite testInvite = inviteList.get(inviteList.size() - 1);
        assertThat(testInvite.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testInvite.getCountryCode()).isEqualTo(DEFAULT_COUNTRY_CODE);
        assertThat(testInvite.getInvitationCode()).isEqualTo(DEFAULT_INVITATION_CODE);
        assertThat(testInvite.getFederationId()).isEqualTo(DEFAULT_FEDERATION_ID);
        assertThat(testInvite.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createInviteWithExistingId() throws Exception {
        // Create the Invite with an existing ID
        invite.setId(1L);
        InviteDTO inviteDTO = inviteMapper.toDto(invite);

        int databaseSizeBeforeCreate = inviteRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInviteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inviteDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Invite in the database
        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = inviteRepository.findAll().size();
        // set the field null
        invite.setPhoneNumber(null);

        // Create the Invite, which fails.
        InviteDTO inviteDTO = inviteMapper.toDto(invite);

        restInviteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inviteDTO)))
            .andExpect(status().isBadRequest());

        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = inviteRepository.findAll().size();
        // set the field null
        invite.setCountryCode(null);

        // Create the Invite, which fails.
        InviteDTO inviteDTO = inviteMapper.toDto(invite);

        restInviteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inviteDTO)))
            .andExpect(status().isBadRequest());

        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkInvitationCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = inviteRepository.findAll().size();
        // set the field null
        invite.setInvitationCode(null);

        // Create the Invite, which fails.
        InviteDTO inviteDTO = inviteMapper.toDto(invite);

        restInviteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inviteDTO)))
            .andExpect(status().isBadRequest());

        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFederationIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = inviteRepository.findAll().size();
        // set the field null
        invite.setFederationId(null);

        // Create the Invite, which fails.
        InviteDTO inviteDTO = inviteMapper.toDto(invite);

        restInviteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inviteDTO)))
            .andExpect(status().isBadRequest());

        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = inviteRepository.findAll().size();
        // set the field null
        invite.setActive(null);

        // Create the Invite, which fails.
        InviteDTO inviteDTO = inviteMapper.toDto(invite);

        restInviteMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inviteDTO)))
            .andExpect(status().isBadRequest());

        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInvites() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList
        restInviteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invite.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].invitationCode").value(hasItem(DEFAULT_INVITATION_CODE)))
            .andExpect(jsonPath("$.[*].federationId").value(hasItem(DEFAULT_FEDERATION_ID.intValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getInvite() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get the invite
        restInviteMockMvc
            .perform(get(ENTITY_API_URL_ID, invite.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(invite.getId().intValue()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.countryCode").value(DEFAULT_COUNTRY_CODE))
            .andExpect(jsonPath("$.invitationCode").value(DEFAULT_INVITATION_CODE))
            .andExpect(jsonPath("$.federationId").value(DEFAULT_FEDERATION_ID.intValue()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getInvitesByIdFiltering() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        Long id = invite.getId();

        defaultInviteShouldBeFound("id.equals=" + id);
        defaultInviteShouldNotBeFound("id.notEquals=" + id);

        defaultInviteShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultInviteShouldNotBeFound("id.greaterThan=" + id);

        defaultInviteShouldBeFound("id.lessThanOrEqual=" + id);
        defaultInviteShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllInvitesByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where phoneNumber equals to DEFAULT_PHONE_NUMBER
        defaultInviteShouldBeFound("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER);

        // Get all the inviteList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultInviteShouldNotBeFound("phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllInvitesByPhoneNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where phoneNumber not equals to DEFAULT_PHONE_NUMBER
        defaultInviteShouldNotBeFound("phoneNumber.notEquals=" + DEFAULT_PHONE_NUMBER);

        // Get all the inviteList where phoneNumber not equals to UPDATED_PHONE_NUMBER
        defaultInviteShouldBeFound("phoneNumber.notEquals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllInvitesByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where phoneNumber in DEFAULT_PHONE_NUMBER or UPDATED_PHONE_NUMBER
        defaultInviteShouldBeFound("phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER);

        // Get all the inviteList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultInviteShouldNotBeFound("phoneNumber.in=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllInvitesByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where phoneNumber is not null
        defaultInviteShouldBeFound("phoneNumber.specified=true");

        // Get all the inviteList where phoneNumber is null
        defaultInviteShouldNotBeFound("phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllInvitesByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where phoneNumber contains DEFAULT_PHONE_NUMBER
        defaultInviteShouldBeFound("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER);

        // Get all the inviteList where phoneNumber contains UPDATED_PHONE_NUMBER
        defaultInviteShouldNotBeFound("phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllInvitesByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where phoneNumber does not contain DEFAULT_PHONE_NUMBER
        defaultInviteShouldNotBeFound("phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER);

        // Get all the inviteList where phoneNumber does not contain UPDATED_PHONE_NUMBER
        defaultInviteShouldBeFound("phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllInvitesByCountryCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where countryCode equals to DEFAULT_COUNTRY_CODE
        defaultInviteShouldBeFound("countryCode.equals=" + DEFAULT_COUNTRY_CODE);

        // Get all the inviteList where countryCode equals to UPDATED_COUNTRY_CODE
        defaultInviteShouldNotBeFound("countryCode.equals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllInvitesByCountryCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where countryCode not equals to DEFAULT_COUNTRY_CODE
        defaultInviteShouldNotBeFound("countryCode.notEquals=" + DEFAULT_COUNTRY_CODE);

        // Get all the inviteList where countryCode not equals to UPDATED_COUNTRY_CODE
        defaultInviteShouldBeFound("countryCode.notEquals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllInvitesByCountryCodeIsInShouldWork() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where countryCode in DEFAULT_COUNTRY_CODE or UPDATED_COUNTRY_CODE
        defaultInviteShouldBeFound("countryCode.in=" + DEFAULT_COUNTRY_CODE + "," + UPDATED_COUNTRY_CODE);

        // Get all the inviteList where countryCode equals to UPDATED_COUNTRY_CODE
        defaultInviteShouldNotBeFound("countryCode.in=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllInvitesByCountryCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where countryCode is not null
        defaultInviteShouldBeFound("countryCode.specified=true");

        // Get all the inviteList where countryCode is null
        defaultInviteShouldNotBeFound("countryCode.specified=false");
    }

    @Test
    @Transactional
    void getAllInvitesByCountryCodeContainsSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where countryCode contains DEFAULT_COUNTRY_CODE
        defaultInviteShouldBeFound("countryCode.contains=" + DEFAULT_COUNTRY_CODE);

        // Get all the inviteList where countryCode contains UPDATED_COUNTRY_CODE
        defaultInviteShouldNotBeFound("countryCode.contains=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllInvitesByCountryCodeNotContainsSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where countryCode does not contain DEFAULT_COUNTRY_CODE
        defaultInviteShouldNotBeFound("countryCode.doesNotContain=" + DEFAULT_COUNTRY_CODE);

        // Get all the inviteList where countryCode does not contain UPDATED_COUNTRY_CODE
        defaultInviteShouldBeFound("countryCode.doesNotContain=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllInvitesByInvitationCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where invitationCode equals to DEFAULT_INVITATION_CODE
        defaultInviteShouldBeFound("invitationCode.equals=" + DEFAULT_INVITATION_CODE);

        // Get all the inviteList where invitationCode equals to UPDATED_INVITATION_CODE
        defaultInviteShouldNotBeFound("invitationCode.equals=" + UPDATED_INVITATION_CODE);
    }

    @Test
    @Transactional
    void getAllInvitesByInvitationCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where invitationCode not equals to DEFAULT_INVITATION_CODE
        defaultInviteShouldNotBeFound("invitationCode.notEquals=" + DEFAULT_INVITATION_CODE);

        // Get all the inviteList where invitationCode not equals to UPDATED_INVITATION_CODE
        defaultInviteShouldBeFound("invitationCode.notEquals=" + UPDATED_INVITATION_CODE);
    }

    @Test
    @Transactional
    void getAllInvitesByInvitationCodeIsInShouldWork() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where invitationCode in DEFAULT_INVITATION_CODE or UPDATED_INVITATION_CODE
        defaultInviteShouldBeFound("invitationCode.in=" + DEFAULT_INVITATION_CODE + "," + UPDATED_INVITATION_CODE);

        // Get all the inviteList where invitationCode equals to UPDATED_INVITATION_CODE
        defaultInviteShouldNotBeFound("invitationCode.in=" + UPDATED_INVITATION_CODE);
    }

    @Test
    @Transactional
    void getAllInvitesByInvitationCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where invitationCode is not null
        defaultInviteShouldBeFound("invitationCode.specified=true");

        // Get all the inviteList where invitationCode is null
        defaultInviteShouldNotBeFound("invitationCode.specified=false");
    }

    @Test
    @Transactional
    void getAllInvitesByInvitationCodeContainsSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where invitationCode contains DEFAULT_INVITATION_CODE
        defaultInviteShouldBeFound("invitationCode.contains=" + DEFAULT_INVITATION_CODE);

        // Get all the inviteList where invitationCode contains UPDATED_INVITATION_CODE
        defaultInviteShouldNotBeFound("invitationCode.contains=" + UPDATED_INVITATION_CODE);
    }

    @Test
    @Transactional
    void getAllInvitesByInvitationCodeNotContainsSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where invitationCode does not contain DEFAULT_INVITATION_CODE
        defaultInviteShouldNotBeFound("invitationCode.doesNotContain=" + DEFAULT_INVITATION_CODE);

        // Get all the inviteList where invitationCode does not contain UPDATED_INVITATION_CODE
        defaultInviteShouldBeFound("invitationCode.doesNotContain=" + UPDATED_INVITATION_CODE);
    }

    @Test
    @Transactional
    void getAllInvitesByFederationIdIsEqualToSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where federationId equals to DEFAULT_FEDERATION_ID
        defaultInviteShouldBeFound("federationId.equals=" + DEFAULT_FEDERATION_ID);

        // Get all the inviteList where federationId equals to UPDATED_FEDERATION_ID
        defaultInviteShouldNotBeFound("federationId.equals=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllInvitesByFederationIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where federationId not equals to DEFAULT_FEDERATION_ID
        defaultInviteShouldNotBeFound("federationId.notEquals=" + DEFAULT_FEDERATION_ID);

        // Get all the inviteList where federationId not equals to UPDATED_FEDERATION_ID
        defaultInviteShouldBeFound("federationId.notEquals=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllInvitesByFederationIdIsInShouldWork() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where federationId in DEFAULT_FEDERATION_ID or UPDATED_FEDERATION_ID
        defaultInviteShouldBeFound("federationId.in=" + DEFAULT_FEDERATION_ID + "," + UPDATED_FEDERATION_ID);

        // Get all the inviteList where federationId equals to UPDATED_FEDERATION_ID
        defaultInviteShouldNotBeFound("federationId.in=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllInvitesByFederationIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where federationId is not null
        defaultInviteShouldBeFound("federationId.specified=true");

        // Get all the inviteList where federationId is null
        defaultInviteShouldNotBeFound("federationId.specified=false");
    }

    @Test
    @Transactional
    void getAllInvitesByFederationIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where federationId is greater than or equal to DEFAULT_FEDERATION_ID
        defaultInviteShouldBeFound("federationId.greaterThanOrEqual=" + DEFAULT_FEDERATION_ID);

        // Get all the inviteList where federationId is greater than or equal to UPDATED_FEDERATION_ID
        defaultInviteShouldNotBeFound("federationId.greaterThanOrEqual=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllInvitesByFederationIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where federationId is less than or equal to DEFAULT_FEDERATION_ID
        defaultInviteShouldBeFound("federationId.lessThanOrEqual=" + DEFAULT_FEDERATION_ID);

        // Get all the inviteList where federationId is less than or equal to SMALLER_FEDERATION_ID
        defaultInviteShouldNotBeFound("federationId.lessThanOrEqual=" + SMALLER_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllInvitesByFederationIdIsLessThanSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where federationId is less than DEFAULT_FEDERATION_ID
        defaultInviteShouldNotBeFound("federationId.lessThan=" + DEFAULT_FEDERATION_ID);

        // Get all the inviteList where federationId is less than UPDATED_FEDERATION_ID
        defaultInviteShouldBeFound("federationId.lessThan=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllInvitesByFederationIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where federationId is greater than DEFAULT_FEDERATION_ID
        defaultInviteShouldNotBeFound("federationId.greaterThan=" + DEFAULT_FEDERATION_ID);

        // Get all the inviteList where federationId is greater than SMALLER_FEDERATION_ID
        defaultInviteShouldBeFound("federationId.greaterThan=" + SMALLER_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllInvitesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where active equals to DEFAULT_ACTIVE
        defaultInviteShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the inviteList where active equals to UPDATED_ACTIVE
        defaultInviteShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllInvitesByActiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where active not equals to DEFAULT_ACTIVE
        defaultInviteShouldNotBeFound("active.notEquals=" + DEFAULT_ACTIVE);

        // Get all the inviteList where active not equals to UPDATED_ACTIVE
        defaultInviteShouldBeFound("active.notEquals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllInvitesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultInviteShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the inviteList where active equals to UPDATED_ACTIVE
        defaultInviteShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllInvitesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        // Get all the inviteList where active is not null
        defaultInviteShouldBeFound("active.specified=true");

        // Get all the inviteList where active is null
        defaultInviteShouldNotBeFound("active.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultInviteShouldBeFound(String filter) throws Exception {
        restInviteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(invite.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].invitationCode").value(hasItem(DEFAULT_INVITATION_CODE)))
            .andExpect(jsonPath("$.[*].federationId").value(hasItem(DEFAULT_FEDERATION_ID.intValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));

        // Check, that the count call also returns 1
        restInviteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultInviteShouldNotBeFound(String filter) throws Exception {
        restInviteMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restInviteMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingInvite() throws Exception {
        // Get the invite
        restInviteMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewInvite() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        int databaseSizeBeforeUpdate = inviteRepository.findAll().size();

        // Update the invite
        Invite updatedInvite = inviteRepository.findById(invite.getId()).get();
        // Disconnect from session so that the updates on updatedInvite are not directly saved in db
        em.detach(updatedInvite);
        updatedInvite
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .countryCode(UPDATED_COUNTRY_CODE)
            .invitationCode(UPDATED_INVITATION_CODE)
            .federationId(UPDATED_FEDERATION_ID)
            .active(UPDATED_ACTIVE);
        InviteDTO inviteDTO = inviteMapper.toDto(updatedInvite);

        restInviteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inviteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inviteDTO))
            )
            .andExpect(status().isOk());

        // Validate the Invite in the database
        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeUpdate);
        Invite testInvite = inviteList.get(inviteList.size() - 1);
        assertThat(testInvite.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testInvite.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testInvite.getInvitationCode()).isEqualTo(UPDATED_INVITATION_CODE);
        assertThat(testInvite.getFederationId()).isEqualTo(UPDATED_FEDERATION_ID);
        assertThat(testInvite.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingInvite() throws Exception {
        int databaseSizeBeforeUpdate = inviteRepository.findAll().size();
        invite.setId(count.incrementAndGet());

        // Create the Invite
        InviteDTO inviteDTO = inviteMapper.toDto(invite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInviteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inviteDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inviteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invite in the database
        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInvite() throws Exception {
        int databaseSizeBeforeUpdate = inviteRepository.findAll().size();
        invite.setId(count.incrementAndGet());

        // Create the Invite
        InviteDTO inviteDTO = inviteMapper.toDto(invite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInviteMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(inviteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invite in the database
        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInvite() throws Exception {
        int databaseSizeBeforeUpdate = inviteRepository.findAll().size();
        invite.setId(count.incrementAndGet());

        // Create the Invite
        InviteDTO inviteDTO = inviteMapper.toDto(invite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInviteMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(inviteDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Invite in the database
        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInviteWithPatch() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        int databaseSizeBeforeUpdate = inviteRepository.findAll().size();

        // Update the invite using partial update
        Invite partialUpdatedInvite = new Invite();
        partialUpdatedInvite.setId(invite.getId());

        partialUpdatedInvite.countryCode(UPDATED_COUNTRY_CODE).invitationCode(UPDATED_INVITATION_CODE).active(UPDATED_ACTIVE);

        restInviteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvite.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInvite))
            )
            .andExpect(status().isOk());

        // Validate the Invite in the database
        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeUpdate);
        Invite testInvite = inviteList.get(inviteList.size() - 1);
        assertThat(testInvite.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testInvite.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testInvite.getInvitationCode()).isEqualTo(UPDATED_INVITATION_CODE);
        assertThat(testInvite.getFederationId()).isEqualTo(DEFAULT_FEDERATION_ID);
        assertThat(testInvite.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateInviteWithPatch() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        int databaseSizeBeforeUpdate = inviteRepository.findAll().size();

        // Update the invite using partial update
        Invite partialUpdatedInvite = new Invite();
        partialUpdatedInvite.setId(invite.getId());

        partialUpdatedInvite
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .countryCode(UPDATED_COUNTRY_CODE)
            .invitationCode(UPDATED_INVITATION_CODE)
            .federationId(UPDATED_FEDERATION_ID)
            .active(UPDATED_ACTIVE);

        restInviteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInvite.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedInvite))
            )
            .andExpect(status().isOk());

        // Validate the Invite in the database
        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeUpdate);
        Invite testInvite = inviteList.get(inviteList.size() - 1);
        assertThat(testInvite.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testInvite.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testInvite.getInvitationCode()).isEqualTo(UPDATED_INVITATION_CODE);
        assertThat(testInvite.getFederationId()).isEqualTo(UPDATED_FEDERATION_ID);
        assertThat(testInvite.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingInvite() throws Exception {
        int databaseSizeBeforeUpdate = inviteRepository.findAll().size();
        invite.setId(count.incrementAndGet());

        // Create the Invite
        InviteDTO inviteDTO = inviteMapper.toDto(invite);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInviteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inviteDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inviteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invite in the database
        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInvite() throws Exception {
        int databaseSizeBeforeUpdate = inviteRepository.findAll().size();
        invite.setId(count.incrementAndGet());

        // Create the Invite
        InviteDTO inviteDTO = inviteMapper.toDto(invite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInviteMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(inviteDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Invite in the database
        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInvite() throws Exception {
        int databaseSizeBeforeUpdate = inviteRepository.findAll().size();
        invite.setId(count.incrementAndGet());

        // Create the Invite
        InviteDTO inviteDTO = inviteMapper.toDto(invite);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInviteMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(inviteDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Invite in the database
        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInvite() throws Exception {
        // Initialize the database
        inviteRepository.saveAndFlush(invite);

        int databaseSizeBeforeDelete = inviteRepository.findAll().size();

        // Delete the invite
        restInviteMockMvc
            .perform(delete(ENTITY_API_URL_ID, invite.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Invite> inviteList = inviteRepository.findAll();
        assertThat(inviteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
