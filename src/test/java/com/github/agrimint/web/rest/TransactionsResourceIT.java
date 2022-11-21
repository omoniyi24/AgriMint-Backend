package com.github.agrimint.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.agrimint.IntegrationTest;
import com.github.agrimint.domain.Transactions;
import com.github.agrimint.domain.enumeration.DRCR;
import com.github.agrimint.domain.enumeration.TransactionType;
import com.github.agrimint.repository.TransactionsRepository;
import com.github.agrimint.service.criteria.TransactionsCriteria;
import com.github.agrimint.service.dto.TransactionsDTO;
import com.github.agrimint.service.mapper.TransactionsMapper;
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
 * Integration tests for the {@link TransactionsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionsResourceIT {

    private static final Long DEFAULT_FEDERATION_ID = 1L;
    private static final Long UPDATED_FEDERATION_ID = 2L;
    private static final Long SMALLER_FEDERATION_ID = 1L - 1L;

    private static final String DEFAULT_MEMBER_ID = "AAAAAAAAAA";
    private static final String UPDATED_MEMBER_ID = "BBBBBBBBBB";

    private static final String DEFAULT_RECEIVER_ID = "AAAAAAAAAA";
    private static final String UPDATED_RECEIVER_ID = "BBBBBBBBBB";

    private static final Long DEFAULT_AMOUNT_IN_SAT = 1L;
    private static final Long UPDATED_AMOUNT_IN_SAT = 2L;
    private static final Long SMALLER_AMOUNT_IN_SAT = 1L - 1L;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final DRCR DEFAULT_DRCR = DRCR.DR;
    private static final DRCR UPDATED_DRCR = DRCR.CR;

    private static final String DEFAULT_TRANSACTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_ID = "BBBBBBBBBB";

    private static final Instant DEFAULT_TRANSACTION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TRANSACTION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final TransactionType DEFAULT_TRANSACTION_TYPE = TransactionType.ON_MINT;
    private static final TransactionType UPDATED_TRANSACTION_TYPE = TransactionType.OFF_MINT;

    private static final String ENTITY_API_URL = "/api/transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Autowired
    private TransactionsMapper transactionsMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionsMockMvc;

    private Transactions transactions;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transactions createEntity(EntityManager em) {
        Transactions transactions = new Transactions()
            .federationId(DEFAULT_FEDERATION_ID)
            .memberId(DEFAULT_MEMBER_ID)
            .receiverId(DEFAULT_RECEIVER_ID)
            .amountInSat(DEFAULT_AMOUNT_IN_SAT)
            .description(DEFAULT_DESCRIPTION)
            .drcr(DEFAULT_DRCR)
            .transactionId(DEFAULT_TRANSACTION_ID)
            .transactionDate(DEFAULT_TRANSACTION_DATE)
            .transactionType(DEFAULT_TRANSACTION_TYPE);
        return transactions;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transactions createUpdatedEntity(EntityManager em) {
        Transactions transactions = new Transactions()
            .federationId(UPDATED_FEDERATION_ID)
            .memberId(UPDATED_MEMBER_ID)
            .receiverId(UPDATED_RECEIVER_ID)
            .amountInSat(UPDATED_AMOUNT_IN_SAT)
            .description(UPDATED_DESCRIPTION)
            .drcr(UPDATED_DRCR)
            .transactionId(UPDATED_TRANSACTION_ID)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .transactionType(UPDATED_TRANSACTION_TYPE);
        return transactions;
    }

    @BeforeEach
    public void initTest() {
        transactions = createEntity(em);
    }

    @Test
    @Transactional
    void createTransactions() throws Exception {
        int databaseSizeBeforeCreate = transactionsRepository.findAll().size();
        // Create the Transactions
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);
        restTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Transactions in the database
        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeCreate + 1);
        Transactions testTransactions = transactionsList.get(transactionsList.size() - 1);
        assertThat(testTransactions.getFederationId()).isEqualTo(DEFAULT_FEDERATION_ID);
        assertThat(testTransactions.getMemberId()).isEqualTo(DEFAULT_MEMBER_ID);
        assertThat(testTransactions.getReceiverId()).isEqualTo(DEFAULT_RECEIVER_ID);
        assertThat(testTransactions.getAmountInSat()).isEqualTo(DEFAULT_AMOUNT_IN_SAT);
        assertThat(testTransactions.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTransactions.getDrcr()).isEqualTo(DEFAULT_DRCR);
        assertThat(testTransactions.getTransactionId()).isEqualTo(DEFAULT_TRANSACTION_ID);
        assertThat(testTransactions.getTransactionDate()).isEqualTo(DEFAULT_TRANSACTION_DATE);
        assertThat(testTransactions.getTransactionType()).isEqualTo(DEFAULT_TRANSACTION_TYPE);
    }

    @Test
    @Transactional
    void createTransactionsWithExistingId() throws Exception {
        // Create the Transactions with an existing ID
        transactions.setId(1L);
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        int databaseSizeBeforeCreate = transactionsRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transactions in the database
        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFederationIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionsRepository.findAll().size();
        // set the field null
        transactions.setFederationId(null);

        // Create the Transactions, which fails.
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        restTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isBadRequest());

        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountInSatIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionsRepository.findAll().size();
        // set the field null
        transactions.setAmountInSat(null);

        // Create the Transactions, which fails.
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        restTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isBadRequest());

        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionsRepository.findAll().size();
        // set the field null
        transactions.setDescription(null);

        // Create the Transactions, which fails.
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        restTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isBadRequest());

        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDrcrIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionsRepository.findAll().size();
        // set the field null
        transactions.setDrcr(null);

        // Create the Transactions, which fails.
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        restTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isBadRequest());

        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTransactionIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionsRepository.findAll().size();
        // set the field null
        transactions.setTransactionId(null);

        // Create the Transactions, which fails.
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        restTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isBadRequest());

        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTransactionDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionsRepository.findAll().size();
        // set the field null
        transactions.setTransactionDate(null);

        // Create the Transactions, which fails.
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        restTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isBadRequest());

        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTransactionTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionsRepository.findAll().size();
        // set the field null
        transactions.setTransactionType(null);

        // Create the Transactions, which fails.
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        restTransactionsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isBadRequest());

        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransactions() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList
        restTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactions.getId().intValue())))
            .andExpect(jsonPath("$.[*].federationId").value(hasItem(DEFAULT_FEDERATION_ID.intValue())))
            .andExpect(jsonPath("$.[*].memberId").value(hasItem(DEFAULT_MEMBER_ID)))
            .andExpect(jsonPath("$.[*].receiverId").value(hasItem(DEFAULT_RECEIVER_ID)))
            .andExpect(jsonPath("$.[*].amountInSat").value(hasItem(DEFAULT_AMOUNT_IN_SAT.intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].drcr").value(hasItem(DEFAULT_DRCR.toString())))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].transactionDate").value(hasItem(DEFAULT_TRANSACTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].transactionType").value(hasItem(DEFAULT_TRANSACTION_TYPE.toString())));
    }

    @Test
    @Transactional
    void getTransactions() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get the transactions
        restTransactionsMockMvc
            .perform(get(ENTITY_API_URL_ID, transactions.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactions.getId().intValue()))
            .andExpect(jsonPath("$.federationId").value(DEFAULT_FEDERATION_ID.intValue()))
            .andExpect(jsonPath("$.memberId").value(DEFAULT_MEMBER_ID))
            .andExpect(jsonPath("$.receiverId").value(DEFAULT_RECEIVER_ID))
            .andExpect(jsonPath("$.amountInSat").value(DEFAULT_AMOUNT_IN_SAT.intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.drcr").value(DEFAULT_DRCR.toString()))
            .andExpect(jsonPath("$.transactionId").value(DEFAULT_TRANSACTION_ID))
            .andExpect(jsonPath("$.transactionDate").value(DEFAULT_TRANSACTION_DATE.toString()))
            .andExpect(jsonPath("$.transactionType").value(DEFAULT_TRANSACTION_TYPE.toString()));
    }

    @Test
    @Transactional
    void getTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        Long id = transactions.getId();

        defaultTransactionsShouldBeFound("id.equals=" + id);
        defaultTransactionsShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionsShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionsShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionsShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionsShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionsByFederationIdIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where federationId equals to DEFAULT_FEDERATION_ID
        defaultTransactionsShouldBeFound("federationId.equals=" + DEFAULT_FEDERATION_ID);

        // Get all the transactionsList where federationId equals to UPDATED_FEDERATION_ID
        defaultTransactionsShouldNotBeFound("federationId.equals=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByFederationIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where federationId not equals to DEFAULT_FEDERATION_ID
        defaultTransactionsShouldNotBeFound("federationId.notEquals=" + DEFAULT_FEDERATION_ID);

        // Get all the transactionsList where federationId not equals to UPDATED_FEDERATION_ID
        defaultTransactionsShouldBeFound("federationId.notEquals=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByFederationIdIsInShouldWork() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where federationId in DEFAULT_FEDERATION_ID or UPDATED_FEDERATION_ID
        defaultTransactionsShouldBeFound("federationId.in=" + DEFAULT_FEDERATION_ID + "," + UPDATED_FEDERATION_ID);

        // Get all the transactionsList where federationId equals to UPDATED_FEDERATION_ID
        defaultTransactionsShouldNotBeFound("federationId.in=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByFederationIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where federationId is not null
        defaultTransactionsShouldBeFound("federationId.specified=true");

        // Get all the transactionsList where federationId is null
        defaultTransactionsShouldNotBeFound("federationId.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByFederationIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where federationId is greater than or equal to DEFAULT_FEDERATION_ID
        defaultTransactionsShouldBeFound("federationId.greaterThanOrEqual=" + DEFAULT_FEDERATION_ID);

        // Get all the transactionsList where federationId is greater than or equal to UPDATED_FEDERATION_ID
        defaultTransactionsShouldNotBeFound("federationId.greaterThanOrEqual=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByFederationIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where federationId is less than or equal to DEFAULT_FEDERATION_ID
        defaultTransactionsShouldBeFound("federationId.lessThanOrEqual=" + DEFAULT_FEDERATION_ID);

        // Get all the transactionsList where federationId is less than or equal to SMALLER_FEDERATION_ID
        defaultTransactionsShouldNotBeFound("federationId.lessThanOrEqual=" + SMALLER_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByFederationIdIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where federationId is less than DEFAULT_FEDERATION_ID
        defaultTransactionsShouldNotBeFound("federationId.lessThan=" + DEFAULT_FEDERATION_ID);

        // Get all the transactionsList where federationId is less than UPDATED_FEDERATION_ID
        defaultTransactionsShouldBeFound("federationId.lessThan=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByFederationIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where federationId is greater than DEFAULT_FEDERATION_ID
        defaultTransactionsShouldNotBeFound("federationId.greaterThan=" + DEFAULT_FEDERATION_ID);

        // Get all the transactionsList where federationId is greater than SMALLER_FEDERATION_ID
        defaultTransactionsShouldBeFound("federationId.greaterThan=" + SMALLER_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByMemberIdIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where memberId equals to DEFAULT_MEMBER_ID
        defaultTransactionsShouldBeFound("memberId.equals=" + DEFAULT_MEMBER_ID);

        // Get all the transactionsList where memberId equals to UPDATED_MEMBER_ID
        defaultTransactionsShouldNotBeFound("memberId.equals=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByMemberIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where memberId not equals to DEFAULT_MEMBER_ID
        defaultTransactionsShouldNotBeFound("memberId.notEquals=" + DEFAULT_MEMBER_ID);

        // Get all the transactionsList where memberId not equals to UPDATED_MEMBER_ID
        defaultTransactionsShouldBeFound("memberId.notEquals=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByMemberIdIsInShouldWork() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where memberId in DEFAULT_MEMBER_ID or UPDATED_MEMBER_ID
        defaultTransactionsShouldBeFound("memberId.in=" + DEFAULT_MEMBER_ID + "," + UPDATED_MEMBER_ID);

        // Get all the transactionsList where memberId equals to UPDATED_MEMBER_ID
        defaultTransactionsShouldNotBeFound("memberId.in=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByMemberIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where memberId is not null
        defaultTransactionsShouldBeFound("memberId.specified=true");

        // Get all the transactionsList where memberId is null
        defaultTransactionsShouldNotBeFound("memberId.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByMemberIdContainsSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where memberId contains DEFAULT_MEMBER_ID
        defaultTransactionsShouldBeFound("memberId.contains=" + DEFAULT_MEMBER_ID);

        // Get all the transactionsList where memberId contains UPDATED_MEMBER_ID
        defaultTransactionsShouldNotBeFound("memberId.contains=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByMemberIdNotContainsSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where memberId does not contain DEFAULT_MEMBER_ID
        defaultTransactionsShouldNotBeFound("memberId.doesNotContain=" + DEFAULT_MEMBER_ID);

        // Get all the transactionsList where memberId does not contain UPDATED_MEMBER_ID
        defaultTransactionsShouldBeFound("memberId.doesNotContain=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverIdIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where receiverId equals to DEFAULT_RECEIVER_ID
        defaultTransactionsShouldBeFound("receiverId.equals=" + DEFAULT_RECEIVER_ID);

        // Get all the transactionsList where receiverId equals to UPDATED_RECEIVER_ID
        defaultTransactionsShouldNotBeFound("receiverId.equals=" + UPDATED_RECEIVER_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where receiverId not equals to DEFAULT_RECEIVER_ID
        defaultTransactionsShouldNotBeFound("receiverId.notEquals=" + DEFAULT_RECEIVER_ID);

        // Get all the transactionsList where receiverId not equals to UPDATED_RECEIVER_ID
        defaultTransactionsShouldBeFound("receiverId.notEquals=" + UPDATED_RECEIVER_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverIdIsInShouldWork() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where receiverId in DEFAULT_RECEIVER_ID or UPDATED_RECEIVER_ID
        defaultTransactionsShouldBeFound("receiverId.in=" + DEFAULT_RECEIVER_ID + "," + UPDATED_RECEIVER_ID);

        // Get all the transactionsList where receiverId equals to UPDATED_RECEIVER_ID
        defaultTransactionsShouldNotBeFound("receiverId.in=" + UPDATED_RECEIVER_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where receiverId is not null
        defaultTransactionsShouldBeFound("receiverId.specified=true");

        // Get all the transactionsList where receiverId is null
        defaultTransactionsShouldNotBeFound("receiverId.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverIdContainsSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where receiverId contains DEFAULT_RECEIVER_ID
        defaultTransactionsShouldBeFound("receiverId.contains=" + DEFAULT_RECEIVER_ID);

        // Get all the transactionsList where receiverId contains UPDATED_RECEIVER_ID
        defaultTransactionsShouldNotBeFound("receiverId.contains=" + UPDATED_RECEIVER_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByReceiverIdNotContainsSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where receiverId does not contain DEFAULT_RECEIVER_ID
        defaultTransactionsShouldNotBeFound("receiverId.doesNotContain=" + DEFAULT_RECEIVER_ID);

        // Get all the transactionsList where receiverId does not contain UPDATED_RECEIVER_ID
        defaultTransactionsShouldBeFound("receiverId.doesNotContain=" + UPDATED_RECEIVER_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountInSatIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where amountInSat equals to DEFAULT_AMOUNT_IN_SAT
        defaultTransactionsShouldBeFound("amountInSat.equals=" + DEFAULT_AMOUNT_IN_SAT);

        // Get all the transactionsList where amountInSat equals to UPDATED_AMOUNT_IN_SAT
        defaultTransactionsShouldNotBeFound("amountInSat.equals=" + UPDATED_AMOUNT_IN_SAT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountInSatIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where amountInSat not equals to DEFAULT_AMOUNT_IN_SAT
        defaultTransactionsShouldNotBeFound("amountInSat.notEquals=" + DEFAULT_AMOUNT_IN_SAT);

        // Get all the transactionsList where amountInSat not equals to UPDATED_AMOUNT_IN_SAT
        defaultTransactionsShouldBeFound("amountInSat.notEquals=" + UPDATED_AMOUNT_IN_SAT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountInSatIsInShouldWork() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where amountInSat in DEFAULT_AMOUNT_IN_SAT or UPDATED_AMOUNT_IN_SAT
        defaultTransactionsShouldBeFound("amountInSat.in=" + DEFAULT_AMOUNT_IN_SAT + "," + UPDATED_AMOUNT_IN_SAT);

        // Get all the transactionsList where amountInSat equals to UPDATED_AMOUNT_IN_SAT
        defaultTransactionsShouldNotBeFound("amountInSat.in=" + UPDATED_AMOUNT_IN_SAT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountInSatIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where amountInSat is not null
        defaultTransactionsShouldBeFound("amountInSat.specified=true");

        // Get all the transactionsList where amountInSat is null
        defaultTransactionsShouldNotBeFound("amountInSat.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountInSatIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where amountInSat is greater than or equal to DEFAULT_AMOUNT_IN_SAT
        defaultTransactionsShouldBeFound("amountInSat.greaterThanOrEqual=" + DEFAULT_AMOUNT_IN_SAT);

        // Get all the transactionsList where amountInSat is greater than or equal to UPDATED_AMOUNT_IN_SAT
        defaultTransactionsShouldNotBeFound("amountInSat.greaterThanOrEqual=" + UPDATED_AMOUNT_IN_SAT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountInSatIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where amountInSat is less than or equal to DEFAULT_AMOUNT_IN_SAT
        defaultTransactionsShouldBeFound("amountInSat.lessThanOrEqual=" + DEFAULT_AMOUNT_IN_SAT);

        // Get all the transactionsList where amountInSat is less than or equal to SMALLER_AMOUNT_IN_SAT
        defaultTransactionsShouldNotBeFound("amountInSat.lessThanOrEqual=" + SMALLER_AMOUNT_IN_SAT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountInSatIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where amountInSat is less than DEFAULT_AMOUNT_IN_SAT
        defaultTransactionsShouldNotBeFound("amountInSat.lessThan=" + DEFAULT_AMOUNT_IN_SAT);

        // Get all the transactionsList where amountInSat is less than UPDATED_AMOUNT_IN_SAT
        defaultTransactionsShouldBeFound("amountInSat.lessThan=" + UPDATED_AMOUNT_IN_SAT);
    }

    @Test
    @Transactional
    void getAllTransactionsByAmountInSatIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where amountInSat is greater than DEFAULT_AMOUNT_IN_SAT
        defaultTransactionsShouldNotBeFound("amountInSat.greaterThan=" + DEFAULT_AMOUNT_IN_SAT);

        // Get all the transactionsList where amountInSat is greater than SMALLER_AMOUNT_IN_SAT
        defaultTransactionsShouldBeFound("amountInSat.greaterThan=" + SMALLER_AMOUNT_IN_SAT);
    }

    @Test
    @Transactional
    void getAllTransactionsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where description equals to DEFAULT_DESCRIPTION
        defaultTransactionsShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the transactionsList where description equals to UPDATED_DESCRIPTION
        defaultTransactionsShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransactionsByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where description not equals to DEFAULT_DESCRIPTION
        defaultTransactionsShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the transactionsList where description not equals to UPDATED_DESCRIPTION
        defaultTransactionsShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransactionsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTransactionsShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the transactionsList where description equals to UPDATED_DESCRIPTION
        defaultTransactionsShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransactionsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where description is not null
        defaultTransactionsShouldBeFound("description.specified=true");

        // Get all the transactionsList where description is null
        defaultTransactionsShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where description contains DEFAULT_DESCRIPTION
        defaultTransactionsShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the transactionsList where description contains UPDATED_DESCRIPTION
        defaultTransactionsShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransactionsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where description does not contain DEFAULT_DESCRIPTION
        defaultTransactionsShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the transactionsList where description does not contain UPDATED_DESCRIPTION
        defaultTransactionsShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTransactionsByDrcrIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where drcr equals to DEFAULT_DRCR
        defaultTransactionsShouldBeFound("drcr.equals=" + DEFAULT_DRCR);

        // Get all the transactionsList where drcr equals to UPDATED_DRCR
        defaultTransactionsShouldNotBeFound("drcr.equals=" + UPDATED_DRCR);
    }

    @Test
    @Transactional
    void getAllTransactionsByDrcrIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where drcr not equals to DEFAULT_DRCR
        defaultTransactionsShouldNotBeFound("drcr.notEquals=" + DEFAULT_DRCR);

        // Get all the transactionsList where drcr not equals to UPDATED_DRCR
        defaultTransactionsShouldBeFound("drcr.notEquals=" + UPDATED_DRCR);
    }

    @Test
    @Transactional
    void getAllTransactionsByDrcrIsInShouldWork() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where drcr in DEFAULT_DRCR or UPDATED_DRCR
        defaultTransactionsShouldBeFound("drcr.in=" + DEFAULT_DRCR + "," + UPDATED_DRCR);

        // Get all the transactionsList where drcr equals to UPDATED_DRCR
        defaultTransactionsShouldNotBeFound("drcr.in=" + UPDATED_DRCR);
    }

    @Test
    @Transactional
    void getAllTransactionsByDrcrIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where drcr is not null
        defaultTransactionsShouldBeFound("drcr.specified=true");

        // Get all the transactionsList where drcr is null
        defaultTransactionsShouldNotBeFound("drcr.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionIdIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionId equals to DEFAULT_TRANSACTION_ID
        defaultTransactionsShouldBeFound("transactionId.equals=" + DEFAULT_TRANSACTION_ID);

        // Get all the transactionsList where transactionId equals to UPDATED_TRANSACTION_ID
        defaultTransactionsShouldNotBeFound("transactionId.equals=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionId not equals to DEFAULT_TRANSACTION_ID
        defaultTransactionsShouldNotBeFound("transactionId.notEquals=" + DEFAULT_TRANSACTION_ID);

        // Get all the transactionsList where transactionId not equals to UPDATED_TRANSACTION_ID
        defaultTransactionsShouldBeFound("transactionId.notEquals=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionIdIsInShouldWork() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionId in DEFAULT_TRANSACTION_ID or UPDATED_TRANSACTION_ID
        defaultTransactionsShouldBeFound("transactionId.in=" + DEFAULT_TRANSACTION_ID + "," + UPDATED_TRANSACTION_ID);

        // Get all the transactionsList where transactionId equals to UPDATED_TRANSACTION_ID
        defaultTransactionsShouldNotBeFound("transactionId.in=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionId is not null
        defaultTransactionsShouldBeFound("transactionId.specified=true");

        // Get all the transactionsList where transactionId is null
        defaultTransactionsShouldNotBeFound("transactionId.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionIdContainsSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionId contains DEFAULT_TRANSACTION_ID
        defaultTransactionsShouldBeFound("transactionId.contains=" + DEFAULT_TRANSACTION_ID);

        // Get all the transactionsList where transactionId contains UPDATED_TRANSACTION_ID
        defaultTransactionsShouldNotBeFound("transactionId.contains=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionIdNotContainsSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionId does not contain DEFAULT_TRANSACTION_ID
        defaultTransactionsShouldNotBeFound("transactionId.doesNotContain=" + DEFAULT_TRANSACTION_ID);

        // Get all the transactionsList where transactionId does not contain UPDATED_TRANSACTION_ID
        defaultTransactionsShouldBeFound("transactionId.doesNotContain=" + UPDATED_TRANSACTION_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionDateIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionDate equals to DEFAULT_TRANSACTION_DATE
        defaultTransactionsShouldBeFound("transactionDate.equals=" + DEFAULT_TRANSACTION_DATE);

        // Get all the transactionsList where transactionDate equals to UPDATED_TRANSACTION_DATE
        defaultTransactionsShouldNotBeFound("transactionDate.equals=" + UPDATED_TRANSACTION_DATE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionDate not equals to DEFAULT_TRANSACTION_DATE
        defaultTransactionsShouldNotBeFound("transactionDate.notEquals=" + DEFAULT_TRANSACTION_DATE);

        // Get all the transactionsList where transactionDate not equals to UPDATED_TRANSACTION_DATE
        defaultTransactionsShouldBeFound("transactionDate.notEquals=" + UPDATED_TRANSACTION_DATE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionDateIsInShouldWork() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionDate in DEFAULT_TRANSACTION_DATE or UPDATED_TRANSACTION_DATE
        defaultTransactionsShouldBeFound("transactionDate.in=" + DEFAULT_TRANSACTION_DATE + "," + UPDATED_TRANSACTION_DATE);

        // Get all the transactionsList where transactionDate equals to UPDATED_TRANSACTION_DATE
        defaultTransactionsShouldNotBeFound("transactionDate.in=" + UPDATED_TRANSACTION_DATE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionDate is not null
        defaultTransactionsShouldBeFound("transactionDate.specified=true");

        // Get all the transactionsList where transactionDate is null
        defaultTransactionsShouldNotBeFound("transactionDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionType equals to DEFAULT_TRANSACTION_TYPE
        defaultTransactionsShouldBeFound("transactionType.equals=" + DEFAULT_TRANSACTION_TYPE);

        // Get all the transactionsList where transactionType equals to UPDATED_TRANSACTION_TYPE
        defaultTransactionsShouldNotBeFound("transactionType.equals=" + UPDATED_TRANSACTION_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionType not equals to DEFAULT_TRANSACTION_TYPE
        defaultTransactionsShouldNotBeFound("transactionType.notEquals=" + DEFAULT_TRANSACTION_TYPE);

        // Get all the transactionsList where transactionType not equals to UPDATED_TRANSACTION_TYPE
        defaultTransactionsShouldBeFound("transactionType.notEquals=" + UPDATED_TRANSACTION_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionTypeIsInShouldWork() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionType in DEFAULT_TRANSACTION_TYPE or UPDATED_TRANSACTION_TYPE
        defaultTransactionsShouldBeFound("transactionType.in=" + DEFAULT_TRANSACTION_TYPE + "," + UPDATED_TRANSACTION_TYPE);

        // Get all the transactionsList where transactionType equals to UPDATED_TRANSACTION_TYPE
        defaultTransactionsShouldNotBeFound("transactionType.in=" + UPDATED_TRANSACTION_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTransactionTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        // Get all the transactionsList where transactionType is not null
        defaultTransactionsShouldBeFound("transactionType.specified=true");

        // Get all the transactionsList where transactionType is null
        defaultTransactionsShouldNotBeFound("transactionType.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionsShouldBeFound(String filter) throws Exception {
        restTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactions.getId().intValue())))
            .andExpect(jsonPath("$.[*].federationId").value(hasItem(DEFAULT_FEDERATION_ID.intValue())))
            .andExpect(jsonPath("$.[*].memberId").value(hasItem(DEFAULT_MEMBER_ID)))
            .andExpect(jsonPath("$.[*].receiverId").value(hasItem(DEFAULT_RECEIVER_ID)))
            .andExpect(jsonPath("$.[*].amountInSat").value(hasItem(DEFAULT_AMOUNT_IN_SAT.intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].drcr").value(hasItem(DEFAULT_DRCR.toString())))
            .andExpect(jsonPath("$.[*].transactionId").value(hasItem(DEFAULT_TRANSACTION_ID)))
            .andExpect(jsonPath("$.[*].transactionDate").value(hasItem(DEFAULT_TRANSACTION_DATE.toString())))
            .andExpect(jsonPath("$.[*].transactionType").value(hasItem(DEFAULT_TRANSACTION_TYPE.toString())));

        // Check, that the count call also returns 1
        restTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionsShouldNotBeFound(String filter) throws Exception {
        restTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionsMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransactions() throws Exception {
        // Get the transactions
        restTransactionsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTransactions() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        int databaseSizeBeforeUpdate = transactionsRepository.findAll().size();

        // Update the transactions
        Transactions updatedTransactions = transactionsRepository.findById(transactions.getId()).get();
        // Disconnect from session so that the updates on updatedTransactions are not directly saved in db
        em.detach(updatedTransactions);
        updatedTransactions
            .federationId(UPDATED_FEDERATION_ID)
            .memberId(UPDATED_MEMBER_ID)
            .receiverId(UPDATED_RECEIVER_ID)
            .amountInSat(UPDATED_AMOUNT_IN_SAT)
            .description(UPDATED_DESCRIPTION)
            .drcr(UPDATED_DRCR)
            .transactionId(UPDATED_TRANSACTION_ID)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .transactionType(UPDATED_TRANSACTION_TYPE);
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(updatedTransactions);

        restTransactionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Transactions in the database
        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeUpdate);
        Transactions testTransactions = transactionsList.get(transactionsList.size() - 1);
        assertThat(testTransactions.getFederationId()).isEqualTo(UPDATED_FEDERATION_ID);
        assertThat(testTransactions.getMemberId()).isEqualTo(UPDATED_MEMBER_ID);
        assertThat(testTransactions.getReceiverId()).isEqualTo(UPDATED_RECEIVER_ID);
        assertThat(testTransactions.getAmountInSat()).isEqualTo(UPDATED_AMOUNT_IN_SAT);
        assertThat(testTransactions.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTransactions.getDrcr()).isEqualTo(UPDATED_DRCR);
        assertThat(testTransactions.getTransactionId()).isEqualTo(UPDATED_TRANSACTION_ID);
        assertThat(testTransactions.getTransactionDate()).isEqualTo(UPDATED_TRANSACTION_DATE);
        assertThat(testTransactions.getTransactionType()).isEqualTo(UPDATED_TRANSACTION_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingTransactions() throws Exception {
        int databaseSizeBeforeUpdate = transactionsRepository.findAll().size();
        transactions.setId(count.incrementAndGet());

        // Create the Transactions
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionsDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transactions in the database
        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransactions() throws Exception {
        int databaseSizeBeforeUpdate = transactionsRepository.findAll().size();
        transactions.setId(count.incrementAndGet());

        // Create the Transactions
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transactions in the database
        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransactions() throws Exception {
        int databaseSizeBeforeUpdate = transactionsRepository.findAll().size();
        transactions.setId(count.incrementAndGet());

        // Create the Transactions
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionsMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transactions in the database
        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransactionsWithPatch() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        int databaseSizeBeforeUpdate = transactionsRepository.findAll().size();

        // Update the transactions using partial update
        Transactions partialUpdatedTransactions = new Transactions();
        partialUpdatedTransactions.setId(transactions.getId());

        partialUpdatedTransactions
            .federationId(UPDATED_FEDERATION_ID)
            .memberId(UPDATED_MEMBER_ID)
            .receiverId(UPDATED_RECEIVER_ID)
            .amountInSat(UPDATED_AMOUNT_IN_SAT)
            .transactionId(UPDATED_TRANSACTION_ID);

        restTransactionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactions.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactions))
            )
            .andExpect(status().isOk());

        // Validate the Transactions in the database
        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeUpdate);
        Transactions testTransactions = transactionsList.get(transactionsList.size() - 1);
        assertThat(testTransactions.getFederationId()).isEqualTo(UPDATED_FEDERATION_ID);
        assertThat(testTransactions.getMemberId()).isEqualTo(UPDATED_MEMBER_ID);
        assertThat(testTransactions.getReceiverId()).isEqualTo(UPDATED_RECEIVER_ID);
        assertThat(testTransactions.getAmountInSat()).isEqualTo(UPDATED_AMOUNT_IN_SAT);
        assertThat(testTransactions.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTransactions.getDrcr()).isEqualTo(DEFAULT_DRCR);
        assertThat(testTransactions.getTransactionId()).isEqualTo(UPDATED_TRANSACTION_ID);
        assertThat(testTransactions.getTransactionDate()).isEqualTo(DEFAULT_TRANSACTION_DATE);
        assertThat(testTransactions.getTransactionType()).isEqualTo(DEFAULT_TRANSACTION_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateTransactionsWithPatch() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        int databaseSizeBeforeUpdate = transactionsRepository.findAll().size();

        // Update the transactions using partial update
        Transactions partialUpdatedTransactions = new Transactions();
        partialUpdatedTransactions.setId(transactions.getId());

        partialUpdatedTransactions
            .federationId(UPDATED_FEDERATION_ID)
            .memberId(UPDATED_MEMBER_ID)
            .receiverId(UPDATED_RECEIVER_ID)
            .amountInSat(UPDATED_AMOUNT_IN_SAT)
            .description(UPDATED_DESCRIPTION)
            .drcr(UPDATED_DRCR)
            .transactionId(UPDATED_TRANSACTION_ID)
            .transactionDate(UPDATED_TRANSACTION_DATE)
            .transactionType(UPDATED_TRANSACTION_TYPE);

        restTransactionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactions.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactions))
            )
            .andExpect(status().isOk());

        // Validate the Transactions in the database
        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeUpdate);
        Transactions testTransactions = transactionsList.get(transactionsList.size() - 1);
        assertThat(testTransactions.getFederationId()).isEqualTo(UPDATED_FEDERATION_ID);
        assertThat(testTransactions.getMemberId()).isEqualTo(UPDATED_MEMBER_ID);
        assertThat(testTransactions.getReceiverId()).isEqualTo(UPDATED_RECEIVER_ID);
        assertThat(testTransactions.getAmountInSat()).isEqualTo(UPDATED_AMOUNT_IN_SAT);
        assertThat(testTransactions.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTransactions.getDrcr()).isEqualTo(UPDATED_DRCR);
        assertThat(testTransactions.getTransactionId()).isEqualTo(UPDATED_TRANSACTION_ID);
        assertThat(testTransactions.getTransactionDate()).isEqualTo(UPDATED_TRANSACTION_DATE);
        assertThat(testTransactions.getTransactionType()).isEqualTo(UPDATED_TRANSACTION_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingTransactions() throws Exception {
        int databaseSizeBeforeUpdate = transactionsRepository.findAll().size();
        transactions.setId(count.incrementAndGet());

        // Create the Transactions
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionsDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transactions in the database
        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransactions() throws Exception {
        int databaseSizeBeforeUpdate = transactionsRepository.findAll().size();
        transactions.setId(count.incrementAndGet());

        // Create the Transactions
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transactions in the database
        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransactions() throws Exception {
        int databaseSizeBeforeUpdate = transactionsRepository.findAll().size();
        transactions.setId(count.incrementAndGet());

        // Create the Transactions
        TransactionsDTO transactionsDTO = transactionsMapper.toDto(transactions);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionsDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transactions in the database
        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransactions() throws Exception {
        // Initialize the database
        transactionsRepository.saveAndFlush(transactions);

        int databaseSizeBeforeDelete = transactionsRepository.findAll().size();

        // Delete the transactions
        restTransactionsMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactions.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Transactions> transactionsList = transactionsRepository.findAll();
        assertThat(transactionsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
