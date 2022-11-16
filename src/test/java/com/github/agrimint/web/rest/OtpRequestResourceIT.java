package com.github.agrimint.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.agrimint.IntegrationTest;
import com.github.agrimint.domain.OtpRequest;
import com.github.agrimint.repository.OtpRequestRepository;
import com.github.agrimint.service.criteria.OtpRequestCriteria;
import com.github.agrimint.service.dto.OtpRequestDTO;
import com.github.agrimint.service.mapper.OtpRequestMapper;
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
 * Integration tests for the {@link OtpRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class OtpRequestResourceIT {

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_OTP = "AAAAAAAAAA";
    private static final String UPDATED_OTP = "BBBBBBBBBB";

    private static final String DEFAULT_OTP_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_OTP_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_OTP_LENGTH = 1;
    private static final Integer UPDATED_OTP_LENGTH = 2;
    private static final Integer SMALLER_OTP_LENGTH = 1 - 1;

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_VALIDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_VALIDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/otp-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OtpRequestRepository otpRequestRepository;

    @Autowired
    private OtpRequestMapper otpRequestMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOtpRequestMockMvc;

    private OtpRequest otpRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OtpRequest createEntity(EntityManager em) {
        OtpRequest otpRequest = new OtpRequest()
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .countryCode(DEFAULT_COUNTRY_CODE)
            .otp(DEFAULT_OTP)
            .otpType(DEFAULT_OTP_TYPE)
            .otpLength(DEFAULT_OTP_LENGTH)
            .status(DEFAULT_STATUS)
            .dateValidated(DEFAULT_DATE_VALIDATED);
        return otpRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OtpRequest createUpdatedEntity(EntityManager em) {
        OtpRequest otpRequest = new OtpRequest()
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .countryCode(UPDATED_COUNTRY_CODE)
            .otp(UPDATED_OTP)
            .otpType(UPDATED_OTP_TYPE)
            .otpLength(UPDATED_OTP_LENGTH)
            .status(UPDATED_STATUS)
            .dateValidated(UPDATED_DATE_VALIDATED);
        return otpRequest;
    }

    @BeforeEach
    public void initTest() {
        otpRequest = createEntity(em);
    }

    @Test
    @Transactional
    void createOtpRequest() throws Exception {
        int databaseSizeBeforeCreate = otpRequestRepository.findAll().size();
        // Create the OtpRequest
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);
        restOtpRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(otpRequestDTO)))
            .andExpect(status().isCreated());

        // Validate the OtpRequest in the database
        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeCreate + 1);
        OtpRequest testOtpRequest = otpRequestList.get(otpRequestList.size() - 1);
        assertThat(testOtpRequest.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testOtpRequest.getCountryCode()).isEqualTo(DEFAULT_COUNTRY_CODE);
        assertThat(testOtpRequest.getOtp()).isEqualTo(DEFAULT_OTP);
        assertThat(testOtpRequest.getOtpType()).isEqualTo(DEFAULT_OTP_TYPE);
        assertThat(testOtpRequest.getOtpLength()).isEqualTo(DEFAULT_OTP_LENGTH);
        assertThat(testOtpRequest.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testOtpRequest.getDateValidated()).isEqualTo(DEFAULT_DATE_VALIDATED);
    }

    @Test
    @Transactional
    void createOtpRequestWithExistingId() throws Exception {
        // Create the OtpRequest with an existing ID
        otpRequest.setId(1L);
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);

        int databaseSizeBeforeCreate = otpRequestRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restOtpRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(otpRequestDTO)))
            .andExpect(status().isBadRequest());

        // Validate the OtpRequest in the database
        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = otpRequestRepository.findAll().size();
        // set the field null
        otpRequest.setPhoneNumber(null);

        // Create the OtpRequest, which fails.
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);

        restOtpRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(otpRequestDTO)))
            .andExpect(status().isBadRequest());

        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = otpRequestRepository.findAll().size();
        // set the field null
        otpRequest.setCountryCode(null);

        // Create the OtpRequest, which fails.
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);

        restOtpRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(otpRequestDTO)))
            .andExpect(status().isBadRequest());

        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOtpIsRequired() throws Exception {
        int databaseSizeBeforeTest = otpRequestRepository.findAll().size();
        // set the field null
        otpRequest.setOtp(null);

        // Create the OtpRequest, which fails.
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);

        restOtpRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(otpRequestDTO)))
            .andExpect(status().isBadRequest());

        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOtpTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = otpRequestRepository.findAll().size();
        // set the field null
        otpRequest.setOtpType(null);

        // Create the OtpRequest, which fails.
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);

        restOtpRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(otpRequestDTO)))
            .andExpect(status().isBadRequest());

        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOtpLengthIsRequired() throws Exception {
        int databaseSizeBeforeTest = otpRequestRepository.findAll().size();
        // set the field null
        otpRequest.setOtpLength(null);

        // Create the OtpRequest, which fails.
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);

        restOtpRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(otpRequestDTO)))
            .andExpect(status().isBadRequest());

        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = otpRequestRepository.findAll().size();
        // set the field null
        otpRequest.setStatus(null);

        // Create the OtpRequest, which fails.
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);

        restOtpRequestMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(otpRequestDTO)))
            .andExpect(status().isBadRequest());

        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllOtpRequests() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList
        restOtpRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(otpRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].otp").value(hasItem(DEFAULT_OTP)))
            .andExpect(jsonPath("$.[*].otpType").value(hasItem(DEFAULT_OTP_TYPE)))
            .andExpect(jsonPath("$.[*].otpLength").value(hasItem(DEFAULT_OTP_LENGTH)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].dateValidated").value(hasItem(DEFAULT_DATE_VALIDATED.toString())));
    }

    @Test
    @Transactional
    void getOtpRequest() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get the otpRequest
        restOtpRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, otpRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(otpRequest.getId().intValue()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.countryCode").value(DEFAULT_COUNTRY_CODE))
            .andExpect(jsonPath("$.otp").value(DEFAULT_OTP))
            .andExpect(jsonPath("$.otpType").value(DEFAULT_OTP_TYPE))
            .andExpect(jsonPath("$.otpLength").value(DEFAULT_OTP_LENGTH))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.dateValidated").value(DEFAULT_DATE_VALIDATED.toString()));
    }

    @Test
    @Transactional
    void getOtpRequestsByIdFiltering() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        Long id = otpRequest.getId();

        defaultOtpRequestShouldBeFound("id.equals=" + id);
        defaultOtpRequestShouldNotBeFound("id.notEquals=" + id);

        defaultOtpRequestShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOtpRequestShouldNotBeFound("id.greaterThan=" + id);

        defaultOtpRequestShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOtpRequestShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where phoneNumber equals to DEFAULT_PHONE_NUMBER
        defaultOtpRequestShouldBeFound("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER);

        // Get all the otpRequestList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultOtpRequestShouldNotBeFound("phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByPhoneNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where phoneNumber not equals to DEFAULT_PHONE_NUMBER
        defaultOtpRequestShouldNotBeFound("phoneNumber.notEquals=" + DEFAULT_PHONE_NUMBER);

        // Get all the otpRequestList where phoneNumber not equals to UPDATED_PHONE_NUMBER
        defaultOtpRequestShouldBeFound("phoneNumber.notEquals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where phoneNumber in DEFAULT_PHONE_NUMBER or UPDATED_PHONE_NUMBER
        defaultOtpRequestShouldBeFound("phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER);

        // Get all the otpRequestList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultOtpRequestShouldNotBeFound("phoneNumber.in=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where phoneNumber is not null
        defaultOtpRequestShouldBeFound("phoneNumber.specified=true");

        // Get all the otpRequestList where phoneNumber is null
        defaultOtpRequestShouldNotBeFound("phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllOtpRequestsByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where phoneNumber contains DEFAULT_PHONE_NUMBER
        defaultOtpRequestShouldBeFound("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER);

        // Get all the otpRequestList where phoneNumber contains UPDATED_PHONE_NUMBER
        defaultOtpRequestShouldNotBeFound("phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where phoneNumber does not contain DEFAULT_PHONE_NUMBER
        defaultOtpRequestShouldNotBeFound("phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER);

        // Get all the otpRequestList where phoneNumber does not contain UPDATED_PHONE_NUMBER
        defaultOtpRequestShouldBeFound("phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByCountryCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where countryCode equals to DEFAULT_COUNTRY_CODE
        defaultOtpRequestShouldBeFound("countryCode.equals=" + DEFAULT_COUNTRY_CODE);

        // Get all the otpRequestList where countryCode equals to UPDATED_COUNTRY_CODE
        defaultOtpRequestShouldNotBeFound("countryCode.equals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByCountryCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where countryCode not equals to DEFAULT_COUNTRY_CODE
        defaultOtpRequestShouldNotBeFound("countryCode.notEquals=" + DEFAULT_COUNTRY_CODE);

        // Get all the otpRequestList where countryCode not equals to UPDATED_COUNTRY_CODE
        defaultOtpRequestShouldBeFound("countryCode.notEquals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByCountryCodeIsInShouldWork() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where countryCode in DEFAULT_COUNTRY_CODE or UPDATED_COUNTRY_CODE
        defaultOtpRequestShouldBeFound("countryCode.in=" + DEFAULT_COUNTRY_CODE + "," + UPDATED_COUNTRY_CODE);

        // Get all the otpRequestList where countryCode equals to UPDATED_COUNTRY_CODE
        defaultOtpRequestShouldNotBeFound("countryCode.in=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByCountryCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where countryCode is not null
        defaultOtpRequestShouldBeFound("countryCode.specified=true");

        // Get all the otpRequestList where countryCode is null
        defaultOtpRequestShouldNotBeFound("countryCode.specified=false");
    }

    @Test
    @Transactional
    void getAllOtpRequestsByCountryCodeContainsSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where countryCode contains DEFAULT_COUNTRY_CODE
        defaultOtpRequestShouldBeFound("countryCode.contains=" + DEFAULT_COUNTRY_CODE);

        // Get all the otpRequestList where countryCode contains UPDATED_COUNTRY_CODE
        defaultOtpRequestShouldNotBeFound("countryCode.contains=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByCountryCodeNotContainsSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where countryCode does not contain DEFAULT_COUNTRY_CODE
        defaultOtpRequestShouldNotBeFound("countryCode.doesNotContain=" + DEFAULT_COUNTRY_CODE);

        // Get all the otpRequestList where countryCode does not contain UPDATED_COUNTRY_CODE
        defaultOtpRequestShouldBeFound("countryCode.doesNotContain=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpIsEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otp equals to DEFAULT_OTP
        defaultOtpRequestShouldBeFound("otp.equals=" + DEFAULT_OTP);

        // Get all the otpRequestList where otp equals to UPDATED_OTP
        defaultOtpRequestShouldNotBeFound("otp.equals=" + UPDATED_OTP);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpIsNotEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otp not equals to DEFAULT_OTP
        defaultOtpRequestShouldNotBeFound("otp.notEquals=" + DEFAULT_OTP);

        // Get all the otpRequestList where otp not equals to UPDATED_OTP
        defaultOtpRequestShouldBeFound("otp.notEquals=" + UPDATED_OTP);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpIsInShouldWork() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otp in DEFAULT_OTP or UPDATED_OTP
        defaultOtpRequestShouldBeFound("otp.in=" + DEFAULT_OTP + "," + UPDATED_OTP);

        // Get all the otpRequestList where otp equals to UPDATED_OTP
        defaultOtpRequestShouldNotBeFound("otp.in=" + UPDATED_OTP);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpIsNullOrNotNull() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otp is not null
        defaultOtpRequestShouldBeFound("otp.specified=true");

        // Get all the otpRequestList where otp is null
        defaultOtpRequestShouldNotBeFound("otp.specified=false");
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpContainsSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otp contains DEFAULT_OTP
        defaultOtpRequestShouldBeFound("otp.contains=" + DEFAULT_OTP);

        // Get all the otpRequestList where otp contains UPDATED_OTP
        defaultOtpRequestShouldNotBeFound("otp.contains=" + UPDATED_OTP);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpNotContainsSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otp does not contain DEFAULT_OTP
        defaultOtpRequestShouldNotBeFound("otp.doesNotContain=" + DEFAULT_OTP);

        // Get all the otpRequestList where otp does not contain UPDATED_OTP
        defaultOtpRequestShouldBeFound("otp.doesNotContain=" + UPDATED_OTP);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpType equals to DEFAULT_OTP_TYPE
        defaultOtpRequestShouldBeFound("otpType.equals=" + DEFAULT_OTP_TYPE);

        // Get all the otpRequestList where otpType equals to UPDATED_OTP_TYPE
        defaultOtpRequestShouldNotBeFound("otpType.equals=" + UPDATED_OTP_TYPE);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpType not equals to DEFAULT_OTP_TYPE
        defaultOtpRequestShouldNotBeFound("otpType.notEquals=" + DEFAULT_OTP_TYPE);

        // Get all the otpRequestList where otpType not equals to UPDATED_OTP_TYPE
        defaultOtpRequestShouldBeFound("otpType.notEquals=" + UPDATED_OTP_TYPE);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpTypeIsInShouldWork() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpType in DEFAULT_OTP_TYPE or UPDATED_OTP_TYPE
        defaultOtpRequestShouldBeFound("otpType.in=" + DEFAULT_OTP_TYPE + "," + UPDATED_OTP_TYPE);

        // Get all the otpRequestList where otpType equals to UPDATED_OTP_TYPE
        defaultOtpRequestShouldNotBeFound("otpType.in=" + UPDATED_OTP_TYPE);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpType is not null
        defaultOtpRequestShouldBeFound("otpType.specified=true");

        // Get all the otpRequestList where otpType is null
        defaultOtpRequestShouldNotBeFound("otpType.specified=false");
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpTypeContainsSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpType contains DEFAULT_OTP_TYPE
        defaultOtpRequestShouldBeFound("otpType.contains=" + DEFAULT_OTP_TYPE);

        // Get all the otpRequestList where otpType contains UPDATED_OTP_TYPE
        defaultOtpRequestShouldNotBeFound("otpType.contains=" + UPDATED_OTP_TYPE);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpTypeNotContainsSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpType does not contain DEFAULT_OTP_TYPE
        defaultOtpRequestShouldNotBeFound("otpType.doesNotContain=" + DEFAULT_OTP_TYPE);

        // Get all the otpRequestList where otpType does not contain UPDATED_OTP_TYPE
        defaultOtpRequestShouldBeFound("otpType.doesNotContain=" + UPDATED_OTP_TYPE);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpLengthIsEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpLength equals to DEFAULT_OTP_LENGTH
        defaultOtpRequestShouldBeFound("otpLength.equals=" + DEFAULT_OTP_LENGTH);

        // Get all the otpRequestList where otpLength equals to UPDATED_OTP_LENGTH
        defaultOtpRequestShouldNotBeFound("otpLength.equals=" + UPDATED_OTP_LENGTH);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpLengthIsNotEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpLength not equals to DEFAULT_OTP_LENGTH
        defaultOtpRequestShouldNotBeFound("otpLength.notEquals=" + DEFAULT_OTP_LENGTH);

        // Get all the otpRequestList where otpLength not equals to UPDATED_OTP_LENGTH
        defaultOtpRequestShouldBeFound("otpLength.notEquals=" + UPDATED_OTP_LENGTH);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpLengthIsInShouldWork() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpLength in DEFAULT_OTP_LENGTH or UPDATED_OTP_LENGTH
        defaultOtpRequestShouldBeFound("otpLength.in=" + DEFAULT_OTP_LENGTH + "," + UPDATED_OTP_LENGTH);

        // Get all the otpRequestList where otpLength equals to UPDATED_OTP_LENGTH
        defaultOtpRequestShouldNotBeFound("otpLength.in=" + UPDATED_OTP_LENGTH);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpLengthIsNullOrNotNull() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpLength is not null
        defaultOtpRequestShouldBeFound("otpLength.specified=true");

        // Get all the otpRequestList where otpLength is null
        defaultOtpRequestShouldNotBeFound("otpLength.specified=false");
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpLengthIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpLength is greater than or equal to DEFAULT_OTP_LENGTH
        defaultOtpRequestShouldBeFound("otpLength.greaterThanOrEqual=" + DEFAULT_OTP_LENGTH);

        // Get all the otpRequestList where otpLength is greater than or equal to UPDATED_OTP_LENGTH
        defaultOtpRequestShouldNotBeFound("otpLength.greaterThanOrEqual=" + UPDATED_OTP_LENGTH);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpLengthIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpLength is less than or equal to DEFAULT_OTP_LENGTH
        defaultOtpRequestShouldBeFound("otpLength.lessThanOrEqual=" + DEFAULT_OTP_LENGTH);

        // Get all the otpRequestList where otpLength is less than or equal to SMALLER_OTP_LENGTH
        defaultOtpRequestShouldNotBeFound("otpLength.lessThanOrEqual=" + SMALLER_OTP_LENGTH);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpLengthIsLessThanSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpLength is less than DEFAULT_OTP_LENGTH
        defaultOtpRequestShouldNotBeFound("otpLength.lessThan=" + DEFAULT_OTP_LENGTH);

        // Get all the otpRequestList where otpLength is less than UPDATED_OTP_LENGTH
        defaultOtpRequestShouldBeFound("otpLength.lessThan=" + UPDATED_OTP_LENGTH);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByOtpLengthIsGreaterThanSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where otpLength is greater than DEFAULT_OTP_LENGTH
        defaultOtpRequestShouldNotBeFound("otpLength.greaterThan=" + DEFAULT_OTP_LENGTH);

        // Get all the otpRequestList where otpLength is greater than SMALLER_OTP_LENGTH
        defaultOtpRequestShouldBeFound("otpLength.greaterThan=" + SMALLER_OTP_LENGTH);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where status equals to DEFAULT_STATUS
        defaultOtpRequestShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the otpRequestList where status equals to UPDATED_STATUS
        defaultOtpRequestShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where status not equals to DEFAULT_STATUS
        defaultOtpRequestShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the otpRequestList where status not equals to UPDATED_STATUS
        defaultOtpRequestShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultOtpRequestShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the otpRequestList where status equals to UPDATED_STATUS
        defaultOtpRequestShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where status is not null
        defaultOtpRequestShouldBeFound("status.specified=true");

        // Get all the otpRequestList where status is null
        defaultOtpRequestShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllOtpRequestsByStatusContainsSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where status contains DEFAULT_STATUS
        defaultOtpRequestShouldBeFound("status.contains=" + DEFAULT_STATUS);

        // Get all the otpRequestList where status contains UPDATED_STATUS
        defaultOtpRequestShouldNotBeFound("status.contains=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByStatusNotContainsSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where status does not contain DEFAULT_STATUS
        defaultOtpRequestShouldNotBeFound("status.doesNotContain=" + DEFAULT_STATUS);

        // Get all the otpRequestList where status does not contain UPDATED_STATUS
        defaultOtpRequestShouldBeFound("status.doesNotContain=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByDateValidatedIsEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where dateValidated equals to DEFAULT_DATE_VALIDATED
        defaultOtpRequestShouldBeFound("dateValidated.equals=" + DEFAULT_DATE_VALIDATED);

        // Get all the otpRequestList where dateValidated equals to UPDATED_DATE_VALIDATED
        defaultOtpRequestShouldNotBeFound("dateValidated.equals=" + UPDATED_DATE_VALIDATED);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByDateValidatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where dateValidated not equals to DEFAULT_DATE_VALIDATED
        defaultOtpRequestShouldNotBeFound("dateValidated.notEquals=" + DEFAULT_DATE_VALIDATED);

        // Get all the otpRequestList where dateValidated not equals to UPDATED_DATE_VALIDATED
        defaultOtpRequestShouldBeFound("dateValidated.notEquals=" + UPDATED_DATE_VALIDATED);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByDateValidatedIsInShouldWork() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where dateValidated in DEFAULT_DATE_VALIDATED or UPDATED_DATE_VALIDATED
        defaultOtpRequestShouldBeFound("dateValidated.in=" + DEFAULT_DATE_VALIDATED + "," + UPDATED_DATE_VALIDATED);

        // Get all the otpRequestList where dateValidated equals to UPDATED_DATE_VALIDATED
        defaultOtpRequestShouldNotBeFound("dateValidated.in=" + UPDATED_DATE_VALIDATED);
    }

    @Test
    @Transactional
    void getAllOtpRequestsByDateValidatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        // Get all the otpRequestList where dateValidated is not null
        defaultOtpRequestShouldBeFound("dateValidated.specified=true");

        // Get all the otpRequestList where dateValidated is null
        defaultOtpRequestShouldNotBeFound("dateValidated.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOtpRequestShouldBeFound(String filter) throws Exception {
        restOtpRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(otpRequest.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].otp").value(hasItem(DEFAULT_OTP)))
            .andExpect(jsonPath("$.[*].otpType").value(hasItem(DEFAULT_OTP_TYPE)))
            .andExpect(jsonPath("$.[*].otpLength").value(hasItem(DEFAULT_OTP_LENGTH)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].dateValidated").value(hasItem(DEFAULT_DATE_VALIDATED.toString())));

        // Check, that the count call also returns 1
        restOtpRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOtpRequestShouldNotBeFound(String filter) throws Exception {
        restOtpRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOtpRequestMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingOtpRequest() throws Exception {
        // Get the otpRequest
        restOtpRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewOtpRequest() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        int databaseSizeBeforeUpdate = otpRequestRepository.findAll().size();

        // Update the otpRequest
        OtpRequest updatedOtpRequest = otpRequestRepository.findById(otpRequest.getId()).get();
        // Disconnect from session so that the updates on updatedOtpRequest are not directly saved in db
        em.detach(updatedOtpRequest);
        updatedOtpRequest
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .countryCode(UPDATED_COUNTRY_CODE)
            .otp(UPDATED_OTP)
            .otpType(UPDATED_OTP_TYPE)
            .otpLength(UPDATED_OTP_LENGTH)
            .status(UPDATED_STATUS)
            .dateValidated(UPDATED_DATE_VALIDATED);
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(updatedOtpRequest);

        restOtpRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, otpRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(otpRequestDTO))
            )
            .andExpect(status().isOk());

        // Validate the OtpRequest in the database
        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeUpdate);
        OtpRequest testOtpRequest = otpRequestList.get(otpRequestList.size() - 1);
        assertThat(testOtpRequest.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testOtpRequest.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testOtpRequest.getOtp()).isEqualTo(UPDATED_OTP);
        assertThat(testOtpRequest.getOtpType()).isEqualTo(UPDATED_OTP_TYPE);
        assertThat(testOtpRequest.getOtpLength()).isEqualTo(UPDATED_OTP_LENGTH);
        assertThat(testOtpRequest.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOtpRequest.getDateValidated()).isEqualTo(UPDATED_DATE_VALIDATED);
    }

    @Test
    @Transactional
    void putNonExistingOtpRequest() throws Exception {
        int databaseSizeBeforeUpdate = otpRequestRepository.findAll().size();
        otpRequest.setId(count.incrementAndGet());

        // Create the OtpRequest
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOtpRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, otpRequestDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(otpRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OtpRequest in the database
        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchOtpRequest() throws Exception {
        int databaseSizeBeforeUpdate = otpRequestRepository.findAll().size();
        otpRequest.setId(count.incrementAndGet());

        // Create the OtpRequest
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOtpRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(otpRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OtpRequest in the database
        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamOtpRequest() throws Exception {
        int databaseSizeBeforeUpdate = otpRequestRepository.findAll().size();
        otpRequest.setId(count.incrementAndGet());

        // Create the OtpRequest
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOtpRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(otpRequestDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the OtpRequest in the database
        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateOtpRequestWithPatch() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        int databaseSizeBeforeUpdate = otpRequestRepository.findAll().size();

        // Update the otpRequest using partial update
        OtpRequest partialUpdatedOtpRequest = new OtpRequest();
        partialUpdatedOtpRequest.setId(otpRequest.getId());

        partialUpdatedOtpRequest.phoneNumber(UPDATED_PHONE_NUMBER).otp(UPDATED_OTP);

        restOtpRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOtpRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOtpRequest))
            )
            .andExpect(status().isOk());

        // Validate the OtpRequest in the database
        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeUpdate);
        OtpRequest testOtpRequest = otpRequestList.get(otpRequestList.size() - 1);
        assertThat(testOtpRequest.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testOtpRequest.getCountryCode()).isEqualTo(DEFAULT_COUNTRY_CODE);
        assertThat(testOtpRequest.getOtp()).isEqualTo(UPDATED_OTP);
        assertThat(testOtpRequest.getOtpType()).isEqualTo(DEFAULT_OTP_TYPE);
        assertThat(testOtpRequest.getOtpLength()).isEqualTo(DEFAULT_OTP_LENGTH);
        assertThat(testOtpRequest.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testOtpRequest.getDateValidated()).isEqualTo(DEFAULT_DATE_VALIDATED);
    }

    @Test
    @Transactional
    void fullUpdateOtpRequestWithPatch() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        int databaseSizeBeforeUpdate = otpRequestRepository.findAll().size();

        // Update the otpRequest using partial update
        OtpRequest partialUpdatedOtpRequest = new OtpRequest();
        partialUpdatedOtpRequest.setId(otpRequest.getId());

        partialUpdatedOtpRequest
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .countryCode(UPDATED_COUNTRY_CODE)
            .otp(UPDATED_OTP)
            .otpType(UPDATED_OTP_TYPE)
            .otpLength(UPDATED_OTP_LENGTH)
            .status(UPDATED_STATUS)
            .dateValidated(UPDATED_DATE_VALIDATED);

        restOtpRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedOtpRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedOtpRequest))
            )
            .andExpect(status().isOk());

        // Validate the OtpRequest in the database
        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeUpdate);
        OtpRequest testOtpRequest = otpRequestList.get(otpRequestList.size() - 1);
        assertThat(testOtpRequest.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testOtpRequest.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testOtpRequest.getOtp()).isEqualTo(UPDATED_OTP);
        assertThat(testOtpRequest.getOtpType()).isEqualTo(UPDATED_OTP_TYPE);
        assertThat(testOtpRequest.getOtpLength()).isEqualTo(UPDATED_OTP_LENGTH);
        assertThat(testOtpRequest.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testOtpRequest.getDateValidated()).isEqualTo(UPDATED_DATE_VALIDATED);
    }

    @Test
    @Transactional
    void patchNonExistingOtpRequest() throws Exception {
        int databaseSizeBeforeUpdate = otpRequestRepository.findAll().size();
        otpRequest.setId(count.incrementAndGet());

        // Create the OtpRequest
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOtpRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, otpRequestDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(otpRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OtpRequest in the database
        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchOtpRequest() throws Exception {
        int databaseSizeBeforeUpdate = otpRequestRepository.findAll().size();
        otpRequest.setId(count.incrementAndGet());

        // Create the OtpRequest
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOtpRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(otpRequestDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the OtpRequest in the database
        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamOtpRequest() throws Exception {
        int databaseSizeBeforeUpdate = otpRequestRepository.findAll().size();
        otpRequest.setId(count.incrementAndGet());

        // Create the OtpRequest
        OtpRequestDTO otpRequestDTO = otpRequestMapper.toDto(otpRequest);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restOtpRequestMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(otpRequestDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the OtpRequest in the database
        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteOtpRequest() throws Exception {
        // Initialize the database
        otpRequestRepository.saveAndFlush(otpRequest);

        int databaseSizeBeforeDelete = otpRequestRepository.findAll().size();

        // Delete the otpRequest
        restOtpRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, otpRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OtpRequest> otpRequestList = otpRequestRepository.findAll();
        assertThat(otpRequestList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
