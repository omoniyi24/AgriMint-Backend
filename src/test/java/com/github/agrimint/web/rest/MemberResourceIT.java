package com.github.agrimint.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.agrimint.IntegrationTest;
import com.github.agrimint.domain.Member;
import com.github.agrimint.repository.MemberRepository;
import com.github.agrimint.service.criteria.MemberCriteria;
import com.github.agrimint.service.dto.MemberDTO;
import com.github.agrimint.service.mapper.MemberMapper;
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
 * Integration tests for the {@link MemberResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MemberResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ALIAS = "AAAAAAAAAA";
    private static final String UPDATED_ALIAS = "BBBBBBBBBB";

    private static final Long DEFAULT_FEDERATION_ID = 1L;
    private static final Long UPDATED_FEDERATION_ID = 2L;
    private static final Long SMALLER_FEDERATION_ID = 1L - 1L;

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Boolean DEFAULT_GUARDIAN = false;
    private static final Boolean UPDATED_GUARDIAN = true;

    private static final Instant DEFAULT_DATE_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/members";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMemberMockMvc;

    private Member member;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Member createEntity(EntityManager em) {
        Member member = new Member()
            .name(DEFAULT_NAME)
            .alias(DEFAULT_ALIAS)
            .federationId(DEFAULT_FEDERATION_ID)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .countryCode(DEFAULT_COUNTRY_CODE)
            .active(DEFAULT_ACTIVE)
            .guardian(DEFAULT_GUARDIAN)
            .dateCreated(DEFAULT_DATE_CREATED);
        return member;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Member createUpdatedEntity(EntityManager em) {
        Member member = new Member()
            .name(UPDATED_NAME)
            .alias(UPDATED_ALIAS)
            .federationId(UPDATED_FEDERATION_ID)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .countryCode(UPDATED_COUNTRY_CODE)
            .active(UPDATED_ACTIVE)
            .guardian(UPDATED_GUARDIAN)
            .dateCreated(UPDATED_DATE_CREATED);
        return member;
    }

    @BeforeEach
    public void initTest() {
        member = createEntity(em);
    }

    @Test
    @Transactional
    void createMember() throws Exception {
        int databaseSizeBeforeCreate = memberRepository.findAll().size();
        // Create the Member
        MemberDTO memberDTO = memberMapper.toDto(member);
        restMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(memberDTO)))
            .andExpect(status().isCreated());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeCreate + 1);
        Member testMember = memberList.get(memberList.size() - 1);
        assertThat(testMember.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testMember.getAlias()).isEqualTo(DEFAULT_ALIAS);
        assertThat(testMember.getFederationId()).isEqualTo(DEFAULT_FEDERATION_ID);
        assertThat(testMember.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testMember.getCountryCode()).isEqualTo(DEFAULT_COUNTRY_CODE);
        assertThat(testMember.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testMember.getGuardian()).isEqualTo(DEFAULT_GUARDIAN);
        assertThat(testMember.getDateCreated()).isEqualTo(DEFAULT_DATE_CREATED);
    }

    @Test
    @Transactional
    void createMemberWithExistingId() throws Exception {
        // Create the Member with an existing ID
        member.setId(1L);
        MemberDTO memberDTO = memberMapper.toDto(member);

        int databaseSizeBeforeCreate = memberRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(memberDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().size();
        // set the field null
        member.setName(null);

        // Create the Member, which fails.
        MemberDTO memberDTO = memberMapper.toDto(member);

        restMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(memberDTO)))
            .andExpect(status().isBadRequest());

        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFederationIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().size();
        // set the field null
        member.setFederationId(null);

        // Create the Member, which fails.
        MemberDTO memberDTO = memberMapper.toDto(member);

        restMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(memberDTO)))
            .andExpect(status().isBadRequest());

        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().size();
        // set the field null
        member.setPhoneNumber(null);

        // Create the Member, which fails.
        MemberDTO memberDTO = memberMapper.toDto(member);

        restMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(memberDTO)))
            .andExpect(status().isBadRequest());

        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().size();
        // set the field null
        member.setCountryCode(null);

        // Create the Member, which fails.
        MemberDTO memberDTO = memberMapper.toDto(member);

        restMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(memberDTO)))
            .andExpect(status().isBadRequest());

        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().size();
        // set the field null
        member.setActive(null);

        // Create the Member, which fails.
        MemberDTO memberDTO = memberMapper.toDto(member);

        restMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(memberDTO)))
            .andExpect(status().isBadRequest());

        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGuardianIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().size();
        // set the field null
        member.setGuardian(null);

        // Create the Member, which fails.
        MemberDTO memberDTO = memberMapper.toDto(member);

        restMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(memberDTO)))
            .andExpect(status().isBadRequest());

        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = memberRepository.findAll().size();
        // set the field null
        member.setDateCreated(null);

        // Create the Member, which fails.
        MemberDTO memberDTO = memberMapper.toDto(member);

        restMemberMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(memberDTO)))
            .andExpect(status().isBadRequest());

        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMembers() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList
        restMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(member.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].alias").value(hasItem(DEFAULT_ALIAS)))
            .andExpect(jsonPath("$.[*].federationId").value(hasItem(DEFAULT_FEDERATION_ID.intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].guardian").value(hasItem(DEFAULT_GUARDIAN.booleanValue())))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())));
    }

    @Test
    @Transactional
    void getMember() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get the member
        restMemberMockMvc
            .perform(get(ENTITY_API_URL_ID, member.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(member.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.alias").value(DEFAULT_ALIAS))
            .andExpect(jsonPath("$.federationId").value(DEFAULT_FEDERATION_ID.intValue()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.countryCode").value(DEFAULT_COUNTRY_CODE))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.guardian").value(DEFAULT_GUARDIAN.booleanValue()))
            .andExpect(jsonPath("$.dateCreated").value(DEFAULT_DATE_CREATED.toString()));
    }

    @Test
    @Transactional
    void getMembersByIdFiltering() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        Long id = member.getId();

        defaultMemberShouldBeFound("id.equals=" + id);
        defaultMemberShouldNotBeFound("id.notEquals=" + id);

        defaultMemberShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultMemberShouldNotBeFound("id.greaterThan=" + id);

        defaultMemberShouldBeFound("id.lessThanOrEqual=" + id);
        defaultMemberShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllMembersByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where name equals to DEFAULT_NAME
        defaultMemberShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the memberList where name equals to UPDATED_NAME
        defaultMemberShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMembersByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where name not equals to DEFAULT_NAME
        defaultMemberShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the memberList where name not equals to UPDATED_NAME
        defaultMemberShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMembersByNameIsInShouldWork() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where name in DEFAULT_NAME or UPDATED_NAME
        defaultMemberShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the memberList where name equals to UPDATED_NAME
        defaultMemberShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMembersByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where name is not null
        defaultMemberShouldBeFound("name.specified=true");

        // Get all the memberList where name is null
        defaultMemberShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllMembersByNameContainsSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where name contains DEFAULT_NAME
        defaultMemberShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the memberList where name contains UPDATED_NAME
        defaultMemberShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMembersByNameNotContainsSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where name does not contain DEFAULT_NAME
        defaultMemberShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the memberList where name does not contain UPDATED_NAME
        defaultMemberShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllMembersByAliasIsEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where alias equals to DEFAULT_ALIAS
        defaultMemberShouldBeFound("alias.equals=" + DEFAULT_ALIAS);

        // Get all the memberList where alias equals to UPDATED_ALIAS
        defaultMemberShouldNotBeFound("alias.equals=" + UPDATED_ALIAS);
    }

    @Test
    @Transactional
    void getAllMembersByAliasIsNotEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where alias not equals to DEFAULT_ALIAS
        defaultMemberShouldNotBeFound("alias.notEquals=" + DEFAULT_ALIAS);

        // Get all the memberList where alias not equals to UPDATED_ALIAS
        defaultMemberShouldBeFound("alias.notEquals=" + UPDATED_ALIAS);
    }

    @Test
    @Transactional
    void getAllMembersByAliasIsInShouldWork() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where alias in DEFAULT_ALIAS or UPDATED_ALIAS
        defaultMemberShouldBeFound("alias.in=" + DEFAULT_ALIAS + "," + UPDATED_ALIAS);

        // Get all the memberList where alias equals to UPDATED_ALIAS
        defaultMemberShouldNotBeFound("alias.in=" + UPDATED_ALIAS);
    }

    @Test
    @Transactional
    void getAllMembersByAliasIsNullOrNotNull() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where alias is not null
        defaultMemberShouldBeFound("alias.specified=true");

        // Get all the memberList where alias is null
        defaultMemberShouldNotBeFound("alias.specified=false");
    }

    @Test
    @Transactional
    void getAllMembersByAliasContainsSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where alias contains DEFAULT_ALIAS
        defaultMemberShouldBeFound("alias.contains=" + DEFAULT_ALIAS);

        // Get all the memberList where alias contains UPDATED_ALIAS
        defaultMemberShouldNotBeFound("alias.contains=" + UPDATED_ALIAS);
    }

    @Test
    @Transactional
    void getAllMembersByAliasNotContainsSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where alias does not contain DEFAULT_ALIAS
        defaultMemberShouldNotBeFound("alias.doesNotContain=" + DEFAULT_ALIAS);

        // Get all the memberList where alias does not contain UPDATED_ALIAS
        defaultMemberShouldBeFound("alias.doesNotContain=" + UPDATED_ALIAS);
    }

    @Test
    @Transactional
    void getAllMembersByFederationIdIsEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where federationId equals to DEFAULT_FEDERATION_ID
        defaultMemberShouldBeFound("federationId.equals=" + DEFAULT_FEDERATION_ID);

        // Get all the memberList where federationId equals to UPDATED_FEDERATION_ID
        defaultMemberShouldNotBeFound("federationId.equals=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllMembersByFederationIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where federationId not equals to DEFAULT_FEDERATION_ID
        defaultMemberShouldNotBeFound("federationId.notEquals=" + DEFAULT_FEDERATION_ID);

        // Get all the memberList where federationId not equals to UPDATED_FEDERATION_ID
        defaultMemberShouldBeFound("federationId.notEquals=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllMembersByFederationIdIsInShouldWork() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where federationId in DEFAULT_FEDERATION_ID or UPDATED_FEDERATION_ID
        defaultMemberShouldBeFound("federationId.in=" + DEFAULT_FEDERATION_ID + "," + UPDATED_FEDERATION_ID);

        // Get all the memberList where federationId equals to UPDATED_FEDERATION_ID
        defaultMemberShouldNotBeFound("federationId.in=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllMembersByFederationIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where federationId is not null
        defaultMemberShouldBeFound("federationId.specified=true");

        // Get all the memberList where federationId is null
        defaultMemberShouldNotBeFound("federationId.specified=false");
    }

    @Test
    @Transactional
    void getAllMembersByFederationIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where federationId is greater than or equal to DEFAULT_FEDERATION_ID
        defaultMemberShouldBeFound("federationId.greaterThanOrEqual=" + DEFAULT_FEDERATION_ID);

        // Get all the memberList where federationId is greater than or equal to UPDATED_FEDERATION_ID
        defaultMemberShouldNotBeFound("federationId.greaterThanOrEqual=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllMembersByFederationIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where federationId is less than or equal to DEFAULT_FEDERATION_ID
        defaultMemberShouldBeFound("federationId.lessThanOrEqual=" + DEFAULT_FEDERATION_ID);

        // Get all the memberList where federationId is less than or equal to SMALLER_FEDERATION_ID
        defaultMemberShouldNotBeFound("federationId.lessThanOrEqual=" + SMALLER_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllMembersByFederationIdIsLessThanSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where federationId is less than DEFAULT_FEDERATION_ID
        defaultMemberShouldNotBeFound("federationId.lessThan=" + DEFAULT_FEDERATION_ID);

        // Get all the memberList where federationId is less than UPDATED_FEDERATION_ID
        defaultMemberShouldBeFound("federationId.lessThan=" + UPDATED_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllMembersByFederationIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where federationId is greater than DEFAULT_FEDERATION_ID
        defaultMemberShouldNotBeFound("federationId.greaterThan=" + DEFAULT_FEDERATION_ID);

        // Get all the memberList where federationId is greater than SMALLER_FEDERATION_ID
        defaultMemberShouldBeFound("federationId.greaterThan=" + SMALLER_FEDERATION_ID);
    }

    @Test
    @Transactional
    void getAllMembersByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where phoneNumber equals to DEFAULT_PHONE_NUMBER
        defaultMemberShouldBeFound("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER);

        // Get all the memberList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultMemberShouldNotBeFound("phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllMembersByPhoneNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where phoneNumber not equals to DEFAULT_PHONE_NUMBER
        defaultMemberShouldNotBeFound("phoneNumber.notEquals=" + DEFAULT_PHONE_NUMBER);

        // Get all the memberList where phoneNumber not equals to UPDATED_PHONE_NUMBER
        defaultMemberShouldBeFound("phoneNumber.notEquals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllMembersByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where phoneNumber in DEFAULT_PHONE_NUMBER or UPDATED_PHONE_NUMBER
        defaultMemberShouldBeFound("phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER);

        // Get all the memberList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultMemberShouldNotBeFound("phoneNumber.in=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllMembersByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where phoneNumber is not null
        defaultMemberShouldBeFound("phoneNumber.specified=true");

        // Get all the memberList where phoneNumber is null
        defaultMemberShouldNotBeFound("phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllMembersByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where phoneNumber contains DEFAULT_PHONE_NUMBER
        defaultMemberShouldBeFound("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER);

        // Get all the memberList where phoneNumber contains UPDATED_PHONE_NUMBER
        defaultMemberShouldNotBeFound("phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllMembersByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where phoneNumber does not contain DEFAULT_PHONE_NUMBER
        defaultMemberShouldNotBeFound("phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER);

        // Get all the memberList where phoneNumber does not contain UPDATED_PHONE_NUMBER
        defaultMemberShouldBeFound("phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllMembersByCountryCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where countryCode equals to DEFAULT_COUNTRY_CODE
        defaultMemberShouldBeFound("countryCode.equals=" + DEFAULT_COUNTRY_CODE);

        // Get all the memberList where countryCode equals to UPDATED_COUNTRY_CODE
        defaultMemberShouldNotBeFound("countryCode.equals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllMembersByCountryCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where countryCode not equals to DEFAULT_COUNTRY_CODE
        defaultMemberShouldNotBeFound("countryCode.notEquals=" + DEFAULT_COUNTRY_CODE);

        // Get all the memberList where countryCode not equals to UPDATED_COUNTRY_CODE
        defaultMemberShouldBeFound("countryCode.notEquals=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllMembersByCountryCodeIsInShouldWork() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where countryCode in DEFAULT_COUNTRY_CODE or UPDATED_COUNTRY_CODE
        defaultMemberShouldBeFound("countryCode.in=" + DEFAULT_COUNTRY_CODE + "," + UPDATED_COUNTRY_CODE);

        // Get all the memberList where countryCode equals to UPDATED_COUNTRY_CODE
        defaultMemberShouldNotBeFound("countryCode.in=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllMembersByCountryCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where countryCode is not null
        defaultMemberShouldBeFound("countryCode.specified=true");

        // Get all the memberList where countryCode is null
        defaultMemberShouldNotBeFound("countryCode.specified=false");
    }

    @Test
    @Transactional
    void getAllMembersByCountryCodeContainsSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where countryCode contains DEFAULT_COUNTRY_CODE
        defaultMemberShouldBeFound("countryCode.contains=" + DEFAULT_COUNTRY_CODE);

        // Get all the memberList where countryCode contains UPDATED_COUNTRY_CODE
        defaultMemberShouldNotBeFound("countryCode.contains=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllMembersByCountryCodeNotContainsSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where countryCode does not contain DEFAULT_COUNTRY_CODE
        defaultMemberShouldNotBeFound("countryCode.doesNotContain=" + DEFAULT_COUNTRY_CODE);

        // Get all the memberList where countryCode does not contain UPDATED_COUNTRY_CODE
        defaultMemberShouldBeFound("countryCode.doesNotContain=" + UPDATED_COUNTRY_CODE);
    }

    @Test
    @Transactional
    void getAllMembersByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where active equals to DEFAULT_ACTIVE
        defaultMemberShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the memberList where active equals to UPDATED_ACTIVE
        defaultMemberShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMembersByActiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where active not equals to DEFAULT_ACTIVE
        defaultMemberShouldNotBeFound("active.notEquals=" + DEFAULT_ACTIVE);

        // Get all the memberList where active not equals to UPDATED_ACTIVE
        defaultMemberShouldBeFound("active.notEquals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMembersByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultMemberShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the memberList where active equals to UPDATED_ACTIVE
        defaultMemberShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllMembersByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where active is not null
        defaultMemberShouldBeFound("active.specified=true");

        // Get all the memberList where active is null
        defaultMemberShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllMembersByGuardianIsEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where guardian equals to DEFAULT_GUARDIAN
        defaultMemberShouldBeFound("guardian.equals=" + DEFAULT_GUARDIAN);

        // Get all the memberList where guardian equals to UPDATED_GUARDIAN
        defaultMemberShouldNotBeFound("guardian.equals=" + UPDATED_GUARDIAN);
    }

    @Test
    @Transactional
    void getAllMembersByGuardianIsNotEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where guardian not equals to DEFAULT_GUARDIAN
        defaultMemberShouldNotBeFound("guardian.notEquals=" + DEFAULT_GUARDIAN);

        // Get all the memberList where guardian not equals to UPDATED_GUARDIAN
        defaultMemberShouldBeFound("guardian.notEquals=" + UPDATED_GUARDIAN);
    }

    @Test
    @Transactional
    void getAllMembersByGuardianIsInShouldWork() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where guardian in DEFAULT_GUARDIAN or UPDATED_GUARDIAN
        defaultMemberShouldBeFound("guardian.in=" + DEFAULT_GUARDIAN + "," + UPDATED_GUARDIAN);

        // Get all the memberList where guardian equals to UPDATED_GUARDIAN
        defaultMemberShouldNotBeFound("guardian.in=" + UPDATED_GUARDIAN);
    }

    @Test
    @Transactional
    void getAllMembersByGuardianIsNullOrNotNull() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where guardian is not null
        defaultMemberShouldBeFound("guardian.specified=true");

        // Get all the memberList where guardian is null
        defaultMemberShouldNotBeFound("guardian.specified=false");
    }

    @Test
    @Transactional
    void getAllMembersByDateCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where dateCreated equals to DEFAULT_DATE_CREATED
        defaultMemberShouldBeFound("dateCreated.equals=" + DEFAULT_DATE_CREATED);

        // Get all the memberList where dateCreated equals to UPDATED_DATE_CREATED
        defaultMemberShouldNotBeFound("dateCreated.equals=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllMembersByDateCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where dateCreated not equals to DEFAULT_DATE_CREATED
        defaultMemberShouldNotBeFound("dateCreated.notEquals=" + DEFAULT_DATE_CREATED);

        // Get all the memberList where dateCreated not equals to UPDATED_DATE_CREATED
        defaultMemberShouldBeFound("dateCreated.notEquals=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllMembersByDateCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where dateCreated in DEFAULT_DATE_CREATED or UPDATED_DATE_CREATED
        defaultMemberShouldBeFound("dateCreated.in=" + DEFAULT_DATE_CREATED + "," + UPDATED_DATE_CREATED);

        // Get all the memberList where dateCreated equals to UPDATED_DATE_CREATED
        defaultMemberShouldNotBeFound("dateCreated.in=" + UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void getAllMembersByDateCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        // Get all the memberList where dateCreated is not null
        defaultMemberShouldBeFound("dateCreated.specified=true");

        // Get all the memberList where dateCreated is null
        defaultMemberShouldNotBeFound("dateCreated.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultMemberShouldBeFound(String filter) throws Exception {
        restMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(member.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].alias").value(hasItem(DEFAULT_ALIAS)))
            .andExpect(jsonPath("$.[*].federationId").value(hasItem(DEFAULT_FEDERATION_ID.intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].countryCode").value(hasItem(DEFAULT_COUNTRY_CODE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].guardian").value(hasItem(DEFAULT_GUARDIAN.booleanValue())))
            .andExpect(jsonPath("$.[*].dateCreated").value(hasItem(DEFAULT_DATE_CREATED.toString())));

        // Check, that the count call also returns 1
        restMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultMemberShouldNotBeFound(String filter) throws Exception {
        restMemberMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restMemberMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingMember() throws Exception {
        // Get the member
        restMemberMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewMember() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        int databaseSizeBeforeUpdate = memberRepository.findAll().size();

        // Update the member
        Member updatedMember = memberRepository.findById(member.getId()).get();
        // Disconnect from session so that the updates on updatedMember are not directly saved in db
        em.detach(updatedMember);
        updatedMember
            .name(UPDATED_NAME)
            .alias(UPDATED_ALIAS)
            .federationId(UPDATED_FEDERATION_ID)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .countryCode(UPDATED_COUNTRY_CODE)
            .active(UPDATED_ACTIVE)
            .guardian(UPDATED_GUARDIAN)
            .dateCreated(UPDATED_DATE_CREATED);
        MemberDTO memberDTO = memberMapper.toDto(updatedMember);

        restMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, memberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(memberDTO))
            )
            .andExpect(status().isOk());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
        Member testMember = memberList.get(memberList.size() - 1);
        assertThat(testMember.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMember.getAlias()).isEqualTo(UPDATED_ALIAS);
        assertThat(testMember.getFederationId()).isEqualTo(UPDATED_FEDERATION_ID);
        assertThat(testMember.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testMember.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testMember.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testMember.getGuardian()).isEqualTo(UPDATED_GUARDIAN);
        assertThat(testMember.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void putNonExistingMember() throws Exception {
        int databaseSizeBeforeUpdate = memberRepository.findAll().size();
        member.setId(count.incrementAndGet());

        // Create the Member
        MemberDTO memberDTO = memberMapper.toDto(member);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, memberDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(memberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMember() throws Exception {
        int databaseSizeBeforeUpdate = memberRepository.findAll().size();
        member.setId(count.incrementAndGet());

        // Create the Member
        MemberDTO memberDTO = memberMapper.toDto(member);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMemberMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(memberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMember() throws Exception {
        int databaseSizeBeforeUpdate = memberRepository.findAll().size();
        member.setId(count.incrementAndGet());

        // Create the Member
        MemberDTO memberDTO = memberMapper.toDto(member);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMemberMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(memberDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMemberWithPatch() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        int databaseSizeBeforeUpdate = memberRepository.findAll().size();

        // Update the member using partial update
        Member partialUpdatedMember = new Member();
        partialUpdatedMember.setId(member.getId());

        partialUpdatedMember.name(UPDATED_NAME).guardian(UPDATED_GUARDIAN);

        restMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMember))
            )
            .andExpect(status().isOk());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
        Member testMember = memberList.get(memberList.size() - 1);
        assertThat(testMember.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMember.getAlias()).isEqualTo(DEFAULT_ALIAS);
        assertThat(testMember.getFederationId()).isEqualTo(DEFAULT_FEDERATION_ID);
        assertThat(testMember.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testMember.getCountryCode()).isEqualTo(DEFAULT_COUNTRY_CODE);
        assertThat(testMember.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testMember.getGuardian()).isEqualTo(UPDATED_GUARDIAN);
        assertThat(testMember.getDateCreated()).isEqualTo(DEFAULT_DATE_CREATED);
    }

    @Test
    @Transactional
    void fullUpdateMemberWithPatch() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        int databaseSizeBeforeUpdate = memberRepository.findAll().size();

        // Update the member using partial update
        Member partialUpdatedMember = new Member();
        partialUpdatedMember.setId(member.getId());

        partialUpdatedMember
            .name(UPDATED_NAME)
            .alias(UPDATED_ALIAS)
            .federationId(UPDATED_FEDERATION_ID)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .countryCode(UPDATED_COUNTRY_CODE)
            .active(UPDATED_ACTIVE)
            .guardian(UPDATED_GUARDIAN)
            .dateCreated(UPDATED_DATE_CREATED);

        restMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMember.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedMember))
            )
            .andExpect(status().isOk());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
        Member testMember = memberList.get(memberList.size() - 1);
        assertThat(testMember.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testMember.getAlias()).isEqualTo(UPDATED_ALIAS);
        assertThat(testMember.getFederationId()).isEqualTo(UPDATED_FEDERATION_ID);
        assertThat(testMember.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testMember.getCountryCode()).isEqualTo(UPDATED_COUNTRY_CODE);
        assertThat(testMember.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testMember.getGuardian()).isEqualTo(UPDATED_GUARDIAN);
        assertThat(testMember.getDateCreated()).isEqualTo(UPDATED_DATE_CREATED);
    }

    @Test
    @Transactional
    void patchNonExistingMember() throws Exception {
        int databaseSizeBeforeUpdate = memberRepository.findAll().size();
        member.setId(count.incrementAndGet());

        // Create the Member
        MemberDTO memberDTO = memberMapper.toDto(member);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, memberDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(memberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMember() throws Exception {
        int databaseSizeBeforeUpdate = memberRepository.findAll().size();
        member.setId(count.incrementAndGet());

        // Create the Member
        MemberDTO memberDTO = memberMapper.toDto(member);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMemberMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(memberDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMember() throws Exception {
        int databaseSizeBeforeUpdate = memberRepository.findAll().size();
        member.setId(count.incrementAndGet());

        // Create the Member
        MemberDTO memberDTO = memberMapper.toDto(member);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMemberMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(memberDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Member in the database
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMember() throws Exception {
        // Initialize the database
        memberRepository.saveAndFlush(member);

        int databaseSizeBeforeDelete = memberRepository.findAll().size();

        // Delete the member
        restMemberMockMvc
            .perform(delete(ENTITY_API_URL_ID, member.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Member> memberList = memberRepository.findAll();
        assertThat(memberList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
