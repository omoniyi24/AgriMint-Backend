package com.github.agrimint.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.agrimint.IntegrationTest;
import com.github.agrimint.domain.AppUser;
import com.github.agrimint.domain.Role;
import com.github.agrimint.repository.AppUserRepository;
import com.github.agrimint.service.AppUserService;
import com.github.agrimint.service.criteria.AppUserCriteria;
import com.github.agrimint.service.dto.AppUserDTO;
import com.github.agrimint.service.mapper.AppUserMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AppUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class AppUserResourceIT {

    private static final String DEFAULT_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_SECRET = "AAAAAAAAAA";
    private static final String UPDATED_SECRET = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_DATE_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/app-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AppUserRepository appUserRepository;

    @Mock
    private AppUserRepository appUserRepositoryMock;

    @Autowired
    private AppUserMapper appUserMapper;

    @Mock
    private AppUserService appUserServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAppUserMockMvc;

    private AppUser appUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createEntity(EntityManager em) {
        AppUser appUser = new AppUser()
            .login(DEFAULT_LOGIN)
            .name(DEFAULT_NAME)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .countryCode(DEFAULT_COUNTRY_CODE)
            .secret(DEFAULT_SECRET)
            .active(DEFAULT_ACTIVE)
            .dateCreated(DEFAULT_DATE_CREATED);
        return appUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AppUser createUpdatedEntity(EntityManager em) {
        AppUser appUser = new AppUser()
            .login(UPDATED_LOGIN)
            .name(UPDATED_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .countryCode(UPDATED_COUNTRY_CODE)
            .secret(UPDATED_SECRET)
            .active(UPDATED_ACTIVE)
            .dateCreated(UPDATED_DATE_CREATED);
        return appUser;
    }

    @BeforeEach
    public void initTest() {
        appUser = createEntity(em);
    }

    @Test
    @Transactional
    void createAppUser() throws Exception {
        int databaseSizeBeforeCreate = appUserRepository.findAll().size();
        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);
        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isCreated());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate + 1);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(testAppUser.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAppUser.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testAppUser.getCountryCode()).isEqualTo(DEFAULT_COUNTRY_CODE);
        assertThat(testAppUser.getSecret()).isEqualTo(DEFAULT_SECRET);
        assertThat(testAppUser.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testAppUser.getDateCreated()).isEqualTo(DEFAULT_DATE_CREATED);
    }

    @Test
    @Transactional
    void createAppUserWithExistingId() throws Exception {
        // Create the AppUser with an existing ID
        appUser.setId(1L);
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        int databaseSizeBeforeCreate = appUserRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLoginIsRequired() throws Exception {
        int databaseSizeBeforeTest = appUserRepository.findAll().size();
        // set the field null
        appUser.setLogin(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = appUserRepository.findAll().size();
        // set the field null
        appUser.setName(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = appUserRepository.findAll().size();
        // set the field null
        appUser.setPhoneNumber(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = appUserRepository.findAll().size();
        // set the field null
        appUser.setCountryCode(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSecretIsRequired() throws Exception {
        int databaseSizeBeforeTest = appUserRepository.findAll().size();
        // set the field null
        appUser.setSecret(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = appUserRepository.findAll().size();
        // set the field null
        appUser.setActive(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = appUserRepository.findAll().size();
        // set the field null
        appUser.setDateCreated(null);

        // Create the AppUser, which fails.
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        restAppUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isBadRequest());

        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAppUsers() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].secret").value(hasItem(DEFAULT_SECRET)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAppUsersWithEagerRelationshipsIsEnabled() throws Exception {
        when(appUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAppUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(appUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllAppUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(appUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restAppUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(appUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getAppUser() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get the appUser
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL_ID, appUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(appUser.getId().intValue()))
            .andExpect(jsonPath("$.login").value(DEFAULT_LOGIN))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.countryCode").value(DEFAULT_COUNTRY_CODE))
            .andExpect(jsonPath("$.secret").value(DEFAULT_SECRET))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.dateCreated").value(DEFAULT_DATE_CREATED.toString()));
    }

    @Test
    @Transactional
    void getAppUsersByIdFiltering() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        Long id = appUser.getId();

        defaultAppUserShouldBeFound("id.equals=" + id);
        defaultAppUserShouldNotBeFound("id.notEquals=" + id);

        defaultAppUserShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultAppUserShouldNotBeFound("id.greaterThan=" + id);

        defaultAppUserShouldBeFound("id.lessThanOrEqual=" + id);
        defaultAppUserShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllAppUsersByLoginIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where login equals to DEFAULT_LOGIN
        defaultAppUserShouldBeFound("login.equals=" + DEFAULT_LOGIN);

        // Get all the appUserList where login equals to UPDATED_LOGIN
        defaultAppUserShouldNotBeFound("login.equals=" + UPDATED_LOGIN);
    }

    @Test
    @Transactional
    void getAllAppUsersByLoginIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where login not equals to DEFAULT_LOGIN
        defaultAppUserShouldNotBeFound("login.notEquals=" + DEFAULT_LOGIN);

        // Get all the appUserList where login not equals to UPDATED_LOGIN
        defaultAppUserShouldBeFound("login.notEquals=" + UPDATED_LOGIN);
    }

    @Test
    @Transactional
    void getAllAppUsersByLoginIsInShouldWork() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where login in DEFAULT_LOGIN or UPDATED_LOGIN
        defaultAppUserShouldBeFound("login.in=" + DEFAULT_LOGIN + "," + UPDATED_LOGIN);

        // Get all the appUserList where login equals to UPDATED_LOGIN
        defaultAppUserShouldNotBeFound("login.in=" + UPDATED_LOGIN);
    }

    @Test
    @Transactional
    void getAllAppUsersByLoginIsNullOrNotNull() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where login is not null
        defaultAppUserShouldBeFound("login.specified=true");

        // Get all the appUserList where login is null
        defaultAppUserShouldNotBeFound("login.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByLoginContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where login contains DEFAULT_LOGIN
        defaultAppUserShouldBeFound("login.contains=" + DEFAULT_LOGIN);

        // Get all the appUserList where login contains UPDATED_LOGIN
        defaultAppUserShouldNotBeFound("login.contains=" + UPDATED_LOGIN);
    }

    @Test
    @Transactional
    void getAllAppUsersByLoginNotContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where login does not contain DEFAULT_LOGIN
        defaultAppUserShouldNotBeFound("login.doesNotContain=" + DEFAULT_LOGIN);

        // Get all the appUserList where login does not contain UPDATED_LOGIN
        defaultAppUserShouldBeFound("login.doesNotContain=" + UPDATED_LOGIN);
    }

    @Test
    @Transactional
    void getAllAppUsersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where name equals to DEFAULT_NAME
        defaultAppUserShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the appUserList where name equals to UPDATED_NAME
        defaultAppUserShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAppUsersByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where name not equals to DEFAULT_NAME
        defaultAppUserShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the appUserList where name not equals to UPDATED_NAME
        defaultAppUserShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAppUsersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where name in DEFAULT_NAME or UPDATED_NAME
        defaultAppUserShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the appUserList where name equals to UPDATED_NAME
        defaultAppUserShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAppUsersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where name is not null
        defaultAppUserShouldBeFound("name.specified=true");

        // Get all the appUserList where name is null
        defaultAppUserShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByNameContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where name contains DEFAULT_NAME
        defaultAppUserShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the appUserList where name contains UPDATED_NAME
        defaultAppUserShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAppUsersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where name does not contain DEFAULT_NAME
        defaultAppUserShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the appUserList where name does not contain UPDATED_NAME
        defaultAppUserShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phoneNumber equals to DEFAULT_PHONE_NUMBER
        defaultAppUserShouldBeFound("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER);

        // Get all the appUserList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultAppUserShouldNotBeFound("phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phoneNumber not equals to DEFAULT_PHONE_NUMBER
        defaultAppUserShouldNotBeFound("phoneNumber.notEquals=" + DEFAULT_PHONE_NUMBER);

        // Get all the appUserList where phoneNumber not equals to UPDATED_PHONE_NUMBER
        defaultAppUserShouldBeFound("phoneNumber.notEquals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phoneNumber in DEFAULT_PHONE_NUMBER or UPDATED_PHONE_NUMBER
        defaultAppUserShouldBeFound("phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER);

        // Get all the appUserList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultAppUserShouldNotBeFound("phoneNumber.in=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phoneNumber is not null
        defaultAppUserShouldBeFound("phoneNumber.specified=true");

        // Get all the appUserList where phoneNumber is null
        defaultAppUserShouldNotBeFound("phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phoneNumber contains DEFAULT_PHONE_NUMBER
        defaultAppUserShouldBeFound("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER);

        // Get all the appUserList where phoneNumber contains UPDATED_PHONE_NUMBER
        defaultAppUserShouldNotBeFound("phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllAppUsersByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where phoneNumber does not contain DEFAULT_PHONE_NUMBER
        defaultAppUserShouldNotBeFound("phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER);

        // Get all the appUserList where phoneNumber does not contain UPDATED_PHONE_NUMBER
        defaultAppUserShouldBeFound("phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllAppUsersByCountryCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where countryCode equals to DEFAULT_COUNTRY_CODE
        defaultAppUserShouldBeFound("countryCode.equals=" + DEFAULT_COUNTRY_CODE);

        // Get all the appUserList where countryCode equals to UPDATED_COUNTRY_CODE
        defaultAppUserShouldNotBeFound("countryCode.equals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllAppUsersByCountryCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where countryCode not equals to DEFAULT_COUNTRY_CODE
        defaultAppUserShouldNotBeFound("countryCode.notEquals=" + DEFAULT_COUNTRY_CODE);

        // Get all the appUserList where countryCode not equals to UPDATED_COUNTRY_CODE
        defaultAppUserShouldBeFound("countryCode.notEquals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllAppUsersByCountryCodeIsInShouldWork() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where countryCode in DEFAULT_COUNTRY_CODE or UPDATED_COUNTRY_CODE
        defaultAppUserShouldBeFound("countryCode.in=" + DEFAULT_COUNTRY_CODE + "," + UPDATED_COUNTRY_CODE);

        // Get all the appUserList where countryCode equals to UPDATED_COUNTRY_CODE
        defaultAppUserShouldNotBeFound("countryCode.in=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllAppUsersByCountryCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where countryCode is not null
        defaultAppUserShouldBeFound("countryCode.specified=true");

        // Get all the appUserList where countryCode is null
        defaultAppUserShouldNotBeFound("countryCode.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByCountryCodeContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where countryCode contains DEFAULT_COUNTRY_CODE
        defaultAppUserShouldBeFound("countryCode.contains=" + DEFAULT_COUNTRY_CODE);

        // Get all the appUserList where countryCode contains UPDATED_COUNTRY_CODE
        defaultAppUserShouldNotBeFound("countryCode.contains=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllAppUsersByCountryCodeNotContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where countryCode does not contain DEFAULT_COUNTRY_CODE
        defaultAppUserShouldNotBeFound("countryCode.doesNotContain=" + DEFAULT_COUNTRY_CODE);

        // Get all the appUserList where countryCode does not contain UPDATED_COUNTRY_CODE
        defaultAppUserShouldBeFound("countryCode.doesNotContain=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllAppUsersBySecretIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where secret equals to DEFAULT_SECRET
        defaultAppUserShouldBeFound("secret.equals=" + DEFAULT_SECRET);

        // Get all the appUserList where secret equals to UPDATED_SECRET
        defaultAppUserShouldNotBeFound("secret.equals=" + UPDATED_SECRET);
    }

    @Test
    @Transactional
    void getAllAppUsersBySecretIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where secret not equals to DEFAULT_SECRET
        defaultAppUserShouldNotBeFound("secret.notEquals=" + DEFAULT_SECRET);

        // Get all the appUserList where secret not equals to UPDATED_SECRET
        defaultAppUserShouldBeFound("secret.notEquals=" + UPDATED_SECRET);
    }

    @Test
    @Transactional
    void getAllAppUsersBySecretIsInShouldWork() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where secret in DEFAULT_SECRET or UPDATED_SECRET
        defaultAppUserShouldBeFound("secret.in=" + DEFAULT_SECRET + "," + UPDATED_SECRET);

        // Get all the appUserList where secret equals to UPDATED_SECRET
        defaultAppUserShouldNotBeFound("secret.in=" + UPDATED_SECRET);
    }

    @Test
    @Transactional
    void getAllAppUsersBySecretIsNullOrNotNull() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where secret is not null
        defaultAppUserShouldBeFound("secret.specified=true");

        // Get all the appUserList where secret is null
        defaultAppUserShouldNotBeFound("secret.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersBySecretContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where secret contains DEFAULT_SECRET
        defaultAppUserShouldBeFound("secret.contains=" + DEFAULT_SECRET);

        // Get all the appUserList where secret contains UPDATED_SECRET
        defaultAppUserShouldNotBeFound("secret.contains=" + UPDATED_SECRET);
    }

    @Test
    @Transactional
    void getAllAppUsersBySecretNotContainsSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where secret does not contain DEFAULT_SECRET
        defaultAppUserShouldNotBeFound("secret.doesNotContain=" + DEFAULT_SECRET);

        // Get all the appUserList where secret does not contain UPDATED_SECRET
        defaultAppUserShouldBeFound("secret.doesNotContain=" + UPDATED_SECRET);
    }

    @Test
    @Transactional
    void getAllAppUsersByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where active equals to DEFAULT_ACTIVE
        defaultAppUserShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the appUserList where active equals to UPDATED_ACTIVE
        defaultAppUserShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllAppUsersByActiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where active not equals to DEFAULT_ACTIVE
        defaultAppUserShouldNotBeFound("active.notEquals=" + DEFAULT_ACTIVE);

        // Get all the appUserList where active not equals to UPDATED_ACTIVE
        defaultAppUserShouldBeFound("active.notEquals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllAppUsersByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultAppUserShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the appUserList where active equals to UPDATED_ACTIVE
        defaultAppUserShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllAppUsersByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where active is not null
        defaultAppUserShouldBeFound("active.specified=true");

        // Get all the appUserList where active is null
        defaultAppUserShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByDateCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where dateCreated equals to DEFAULT_DATE_CREATED
        defaultAppUserShouldBeFound("dateCreated.equals=" + DEFAULT_DATE_CREATED);

        // Get all the appUserList where dateCreated equals to UPDATED_DATE_CREATED
        defaultAppUserShouldNotBeFound("dateCreated.equals=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllAppUsersByDateCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where dateCreated not equals to DEFAULT_DATE_CREATED
        defaultAppUserShouldNotBeFound("dateCreated.notEquals=" + DEFAULT_DATE_CREATED);

        // Get all the appUserList where dateCreated not equals to UPDATED_DATE_CREATED
        defaultAppUserShouldBeFound("dateCreated.notEquals=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllAppUsersByDateCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where dateCreated in DEFAULT_DATE_CREATED or UPDATED_DATE_CREATED
        defaultAppUserShouldBeFound("dateCreated.in=" + DEFAULT_DATE_CREATED + "," + UPDATED_DATE_CREATED);

        // Get all the appUserList where dateCreated equals to UPDATED_DATE_CREATED
        defaultAppUserShouldNotBeFound("dateCreated.in=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllAppUsersByDateCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        // Get all the appUserList where dateCreated is not null
        defaultAppUserShouldBeFound("dateCreated.specified=true");

        // Get all the appUserList where dateCreated is null
        defaultAppUserShouldNotBeFound("dateCreated.specified=false");
    }

    @Test
    @Transactional
    void getAllAppUsersByAuthoritiesIsEqualToSomething() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);
        Role authorities = RoleResourceIT.createEntity(em);
        em.persist(authorities);
        em.flush();
        appUser.addAuthorities(authorities);
        appUserRepository.saveAndFlush(appUser);
        Long authoritiesId = authorities.getId();

        // Get all the appUserList where authorities equals to authoritiesId
        defaultAppUserShouldBeFound("authoritiesId.equals=" + authoritiesId);

        // Get all the appUserList where authorities equals to (authoritiesId + 1)
        defaultAppUserShouldNotBeFound("authoritiesId.equals=" + (authoritiesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultAppUserShouldBeFound(String filter) throws Exception {
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(appUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].secret").value(hasItem(DEFAULT_SECRET)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())));

        // Check, that the count call also returns 1
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultAppUserShouldNotBeFound(String filter) throws Exception {
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restAppUserMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingAppUser() throws Exception {
        // Get the appUser
        restAppUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewAppUser() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();

        // Update the appUser
        AppUser updatedAppUser = appUserRepository.findById(appUser.getId()).get();
        // Disconnect from session so that the updates on updatedAppUser are not directly saved in db
        em.detach(updatedAppUser);
        updatedAppUser
            .login(UPDATED_LOGIN)
            .name(UPDATED_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .countryCode(UPDATED_COUNTRY_CODE)
            .secret(UPDATED_SECRET)
            .active(UPDATED_ACTIVE)
            .dateCreated(UPDATED_DATE_CREATED);
        AppUserDTO appUserDTO = appUserMapper.toDto(updatedAppUser);

        restAppUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appUserDTO))
            )
            .andExpect(status().isOk());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getLogin()).isEqualTo(UPDATED_LOGIN);
        assertThat(testAppUser.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAppUser.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testAppUser.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testAppUser.getSecret()).isEqualTo(UPDATED_SECRET);
        assertThat(testAppUser.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testAppUser.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void putNonExistingAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();
        appUser.setId(count.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, appUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();
        appUser.setId(count.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(appUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();
        appUser.setId(count.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(appUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser.name(UPDATED_NAME).countryCode(UPDATED_COUNTRY_CODE).active(UPDATED_ACTIVE).dateCreated(UPDATED_DATE_CREATED);

        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppUser))
            )
            .andExpect(status().isOk());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(testAppUser.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAppUser.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testAppUser.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testAppUser.getSecret()).isEqualTo(DEFAULT_SECRET);
        assertThat(testAppUser.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testAppUser.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void fullUpdateAppUserWithPatch() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();

        // Update the appUser using partial update
        AppUser partialUpdatedAppUser = new AppUser();
        partialUpdatedAppUser.setId(appUser.getId());

        partialUpdatedAppUser
            .login(UPDATED_LOGIN)
            .name(UPDATED_NAME)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .countryCode(UPDATED_COUNTRY_CODE)
            .secret(UPDATED_SECRET)
            .active(UPDATED_ACTIVE)
            .dateCreated(UPDATED_DATE_CREATED);

        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAppUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedAppUser))
            )
            .andExpect(status().isOk());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
        AppUser testAppUser = appUserList.get(appUserList.size() - 1);
        assertThat(testAppUser.getLogin()).isEqualTo(UPDATED_LOGIN);
        assertThat(testAppUser.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAppUser.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testAppUser.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testAppUser.getSecret()).isEqualTo(UPDATED_SECRET);
        assertThat(testAppUser.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testAppUser.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void patchNonExistingAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();
        appUser.setId(count.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, appUserDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();
        appUser.setId(count.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(appUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAppUser() throws Exception {
        int databaseSizeBeforeUpdate = appUserRepository.findAll().size();
        appUser.setId(count.incrementAndGet());

        // Create the AppUser
        AppUserDTO appUserDTO = appUserMapper.toDto(appUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAppUserMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(appUserDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the AppUser in the database
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAppUser() throws Exception {
        // Initialize the database
        appUserRepository.saveAndFlush(appUser);

        int databaseSizeBeforeDelete = appUserRepository.findAll().size();

        // Delete the appUser
        restAppUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, appUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<AppUser> appUserList = appUserRepository.findAll();
        assertThat(appUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
