package com.github.agrimint.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.agrimint.IntegrationTest;
import com.github.agrimint.domain.Guardian;
import com.github.agrimint.repository.GuardianRepository;
import com.github.agrimint.service.criteria.GuardianCriteria;
import com.github.agrimint.service.dto.GuardianDTO;
import com.github.agrimint.service.mapper.GuardianMapper;
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
 * Integration tests for the {@link GuardianResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GuardianResourceIT {

    private static final Long DEFAULT_MEMBER_ID = 1L;
    private static final Long UPDATED_MEMBER_ID = 2L;
    private static final Long SMALLER_MEMBER_ID = 1L - 1L;

    private static final Boolean DEFAULT_INVITATION_SENT = false;
    private static final Boolean UPDATED_INVITATION_SENT = true;

    private static final Boolean DEFAULT_INVITATION_ACCEPTED = false;
    private static final Boolean UPDATED_INVITATION_ACCEPTED = true;

    private static final String ENTITY_API_URL = "/api/guardians";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GuardianRepository guardianRepository;

    @Autowired
    private GuardianMapper guardianMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGuardianMockMvc;

    private Guardian guardian;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Guardian createEntity(EntityManager em) {
        Guardian guardian = new Guardian()
            .memberId(DEFAULT_MEMBER_ID)
            .invitationSent(DEFAULT_INVITATION_SENT)
            .invitationAccepted(DEFAULT_INVITATION_ACCEPTED);
        return guardian;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Guardian createUpdatedEntity(EntityManager em) {
        Guardian guardian = new Guardian()
            .memberId(UPDATED_MEMBER_ID)
            .invitationSent(UPDATED_INVITATION_SENT)
            .invitationAccepted(UPDATED_INVITATION_ACCEPTED);
        return guardian;
    }

    @BeforeEach
    public void initTest() {
        guardian = createEntity(em);
    }

    @Test
    @Transactional
    void createGuardian() throws Exception {
        int databaseSizeBeforeCreate = guardianRepository.findAll().size();
        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);
        restGuardianMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(guardianDTO)))
            .andExpect(status().isCreated());

        // Validate the Guardian in the database
        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeCreate + 1);
        Guardian testGuardian = guardianList.get(guardianList.size() - 1);
        assertThat(testGuardian.getMemberId()).isEqualTo(DEFAULT_MEMBER_ID);
        assertThat(testGuardian.getInvitationSent()).isEqualTo(DEFAULT_INVITATION_SENT);
        assertThat(testGuardian.getInvitationAccepted()).isEqualTo(DEFAULT_INVITATION_ACCEPTED);
    }

    @Test
    @Transactional
    void createGuardianWithExistingId() throws Exception {
        // Create the Guardian with an existing ID
        guardian.setId(1L);
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        int databaseSizeBeforeCreate = guardianRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGuardianMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(guardianDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Guardian in the database
        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkMemberIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = guardianRepository.findAll().size();
        // set the field null
        guardian.setMemberId(null);

        // Create the Guardian, which fails.
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        restGuardianMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(guardianDTO)))
            .andExpect(status().isBadRequest());

        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkInvitationSentIsRequired() throws Exception {
        int databaseSizeBeforeTest = guardianRepository.findAll().size();
        // set the field null
        guardian.setInvitationSent(null);

        // Create the Guardian, which fails.
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        restGuardianMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(guardianDTO)))
            .andExpect(status().isBadRequest());

        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkInvitationAcceptedIsRequired() throws Exception {
        int databaseSizeBeforeTest = guardianRepository.findAll().size();
        // set the field null
        guardian.setInvitationAccepted(null);

        // Create the Guardian, which fails.
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        restGuardianMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(guardianDTO)))
            .andExpect(status().isBadRequest());

        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllGuardians() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList
        restGuardianMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(guardian.getId().intValue())))
            .andExpect(jsonPath("$.[*].memberId").value(hasItem(DEFAULT_MEMBER_ID.intValue())))
            .andExpect(jsonPath("$.[*].invitationSent").value(hasItem(DEFAULT_INVITATION_SENT.booleanValue())))
            .andExpect(jsonPath("$.[*].invitationAccepted").value(hasItem(DEFAULT_INVITATION_ACCEPTED.booleanValue())));
    }

    @Test
    @Transactional
    void getGuardian() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get the guardian
        restGuardianMockMvc
            .perform(get(ENTITY_API_URL_ID, guardian.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(guardian.getId().intValue()))
            .andExpect(jsonPath("$.memberId").value(DEFAULT_MEMBER_ID.intValue()))
            .andExpect(jsonPath("$.invitationSent").value(DEFAULT_INVITATION_SENT.booleanValue()))
            .andExpect(jsonPath("$.invitationAccepted").value(DEFAULT_INVITATION_ACCEPTED.booleanValue()));
    }

    @Test
    @Transactional
    void getGuardiansByIdFiltering() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        Long id = guardian.getId();

        defaultGuardianShouldBeFound("id.equals=" + id);
        defaultGuardianShouldNotBeFound("id.notEquals=" + id);

        defaultGuardianShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultGuardianShouldNotBeFound("id.greaterThan=" + id);

        defaultGuardianShouldBeFound("id.lessThanOrEqual=" + id);
        defaultGuardianShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllGuardiansByMemberIdIsEqualToSomething() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where memberId equals to DEFAULT_MEMBER_ID
        defaultGuardianShouldBeFound("memberId.equals=" + DEFAULT_MEMBER_ID);

        // Get all the guardianList where memberId equals to UPDATED_MEMBER_ID
        defaultGuardianShouldNotBeFound("memberId.equals=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllGuardiansByMemberIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where memberId not equals to DEFAULT_MEMBER_ID
        defaultGuardianShouldNotBeFound("memberId.notEquals=" + DEFAULT_MEMBER_ID);

        // Get all the guardianList where memberId not equals to UPDATED_MEMBER_ID
        defaultGuardianShouldBeFound("memberId.notEquals=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllGuardiansByMemberIdIsInShouldWork() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where memberId in DEFAULT_MEMBER_ID or UPDATED_MEMBER_ID
        defaultGuardianShouldBeFound("memberId.in=" + DEFAULT_MEMBER_ID + "," + UPDATED_MEMBER_ID);

        // Get all the guardianList where memberId equals to UPDATED_MEMBER_ID
        defaultGuardianShouldNotBeFound("memberId.in=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllGuardiansByMemberIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where memberId is not null
        defaultGuardianShouldBeFound("memberId.specified=true");

        // Get all the guardianList where memberId is null
        defaultGuardianShouldNotBeFound("memberId.specified=false");
    }

    @Test
    @Transactional
    void getAllGuardiansByMemberIdIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where memberId is greater than or equal to DEFAULT_MEMBER_ID
        defaultGuardianShouldBeFound("memberId.greaterThanOrEqual=" + DEFAULT_MEMBER_ID);

        // Get all the guardianList where memberId is greater than or equal to UPDATED_MEMBER_ID
        defaultGuardianShouldNotBeFound("memberId.greaterThanOrEqual=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllGuardiansByMemberIdIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where memberId is less than or equal to DEFAULT_MEMBER_ID
        defaultGuardianShouldBeFound("memberId.lessThanOrEqual=" + DEFAULT_MEMBER_ID);

        // Get all the guardianList where memberId is less than or equal to SMALLER_MEMBER_ID
        defaultGuardianShouldNotBeFound("memberId.lessThanOrEqual=" + SMALLER_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllGuardiansByMemberIdIsLessThanSomething() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where memberId is less than DEFAULT_MEMBER_ID
        defaultGuardianShouldNotBeFound("memberId.lessThan=" + DEFAULT_MEMBER_ID);

        // Get all the guardianList where memberId is less than UPDATED_MEMBER_ID
        defaultGuardianShouldBeFound("memberId.lessThan=" + UPDATED_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllGuardiansByMemberIdIsGreaterThanSomething() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where memberId is greater than DEFAULT_MEMBER_ID
        defaultGuardianShouldNotBeFound("memberId.greaterThan=" + DEFAULT_MEMBER_ID);

        // Get all the guardianList where memberId is greater than SMALLER_MEMBER_ID
        defaultGuardianShouldBeFound("memberId.greaterThan=" + SMALLER_MEMBER_ID);
    }

    @Test
    @Transactional
    void getAllGuardiansByInvitationSentIsEqualToSomething() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where invitationSent equals to DEFAULT_INVITATION_SENT
        defaultGuardianShouldBeFound("invitationSent.equals=" + DEFAULT_INVITATION_SENT);

        // Get all the guardianList where invitationSent equals to UPDATED_INVITATION_SENT
        defaultGuardianShouldNotBeFound("invitationSent.equals=" + UPDATED_INVITATION_SENT);
    }

    @Test
    @Transactional
    void getAllGuardiansByInvitationSentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where invitationSent not equals to DEFAULT_INVITATION_SENT
        defaultGuardianShouldNotBeFound("invitationSent.notEquals=" + DEFAULT_INVITATION_SENT);

        // Get all the guardianList where invitationSent not equals to UPDATED_INVITATION_SENT
        defaultGuardianShouldBeFound("invitationSent.notEquals=" + UPDATED_INVITATION_SENT);
    }

    @Test
    @Transactional
    void getAllGuardiansByInvitationSentIsInShouldWork() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where invitationSent in DEFAULT_INVITATION_SENT or UPDATED_INVITATION_SENT
        defaultGuardianShouldBeFound("invitationSent.in=" + DEFAULT_INVITATION_SENT + "," + UPDATED_INVITATION_SENT);

        // Get all the guardianList where invitationSent equals to UPDATED_INVITATION_SENT
        defaultGuardianShouldNotBeFound("invitationSent.in=" + UPDATED_INVITATION_SENT);
    }

    @Test
    @Transactional
    void getAllGuardiansByInvitationSentIsNullOrNotNull() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where invitationSent is not null
        defaultGuardianShouldBeFound("invitationSent.specified=true");

        // Get all the guardianList where invitationSent is null
        defaultGuardianShouldNotBeFound("invitationSent.specified=false");
    }

    @Test
    @Transactional
    void getAllGuardiansByInvitationAcceptedIsEqualToSomething() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where invitationAccepted equals to DEFAULT_INVITATION_ACCEPTED
        defaultGuardianShouldBeFound("invitationAccepted.equals=" + DEFAULT_INVITATION_ACCEPTED);

        // Get all the guardianList where invitationAccepted equals to UPDATED_INVITATION_ACCEPTED
        defaultGuardianShouldNotBeFound("invitationAccepted.equals=" + UPDATED_INVITATION_ACCEPTED);
    }

    @Test
    @Transactional
    void getAllGuardiansByInvitationAcceptedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where invitationAccepted not equals to DEFAULT_INVITATION_ACCEPTED
        defaultGuardianShouldNotBeFound("invitationAccepted.notEquals=" + DEFAULT_INVITATION_ACCEPTED);

        // Get all the guardianList where invitationAccepted not equals to UPDATED_INVITATION_ACCEPTED
        defaultGuardianShouldBeFound("invitationAccepted.notEquals=" + UPDATED_INVITATION_ACCEPTED);
    }

    @Test
    @Transactional
    void getAllGuardiansByInvitationAcceptedIsInShouldWork() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where invitationAccepted in DEFAULT_INVITATION_ACCEPTED or UPDATED_INVITATION_ACCEPTED
        defaultGuardianShouldBeFound("invitationAccepted.in=" + DEFAULT_INVITATION_ACCEPTED + "," + UPDATED_INVITATION_ACCEPTED);

        // Get all the guardianList where invitationAccepted equals to UPDATED_INVITATION_ACCEPTED
        defaultGuardianShouldNotBeFound("invitationAccepted.in=" + UPDATED_INVITATION_ACCEPTED);
    }

    @Test
    @Transactional
    void getAllGuardiansByInvitationAcceptedIsNullOrNotNull() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        // Get all the guardianList where invitationAccepted is not null
        defaultGuardianShouldBeFound("invitationAccepted.specified=true");

        // Get all the guardianList where invitationAccepted is null
        defaultGuardianShouldNotBeFound("invitationAccepted.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultGuardianShouldBeFound(String filter) throws Exception {
        restGuardianMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(guardian.getId().intValue())))
            .andExpect(jsonPath("$.[*].memberId").value(hasItem(DEFAULT_MEMBER_ID.intValue())))
            .andExpect(jsonPath("$.[*].invitationSent").value(hasItem(DEFAULT_INVITATION_SENT.booleanValue())))
            .andExpect(jsonPath("$.[*].invitationAccepted").value(hasItem(DEFAULT_INVITATION_ACCEPTED.booleanValue())));

        // Check, that the count call also returns 1
        restGuardianMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultGuardianShouldNotBeFound(String filter) throws Exception {
        restGuardianMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restGuardianMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingGuardian() throws Exception {
        // Get the guardian
        restGuardianMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGuardian() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        int databaseSizeBeforeUpdate = guardianRepository.findAll().size();

        // Update the guardian
        Guardian updatedGuardian = guardianRepository.findById(guardian.getId()).get();
        // Disconnect from session so that the updates on updatedGuardian are not directly saved in db
        em.detach(updatedGuardian);
        updatedGuardian.memberId(UPDATED_MEMBER_ID).invitationSent(UPDATED_INVITATION_SENT).invitationAccepted(UPDATED_INVITATION_ACCEPTED);
        GuardianDTO guardianDTO = guardianMapper.toDto(updatedGuardian);

        restGuardianMockMvc
            .perform(
                put(ENTITY_API_URL_ID, guardianDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(guardianDTO))
            )
            .andExpect(status().isOk());

        // Validate the Guardian in the database
        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeUpdate);
        Guardian testGuardian = guardianList.get(guardianList.size() - 1);
        assertThat(testGuardian.getMemberId()).isEqualTo(UPDATED_MEMBER_ID);
        assertThat(testGuardian.getInvitationSent()).isEqualTo(UPDATED_INVITATION_SENT);
        assertThat(testGuardian.getInvitationAccepted()).isEqualTo(UPDATED_INVITATION_ACCEPTED);
    }

    @Test
    @Transactional
    void putNonExistingGuardian() throws Exception {
        int databaseSizeBeforeUpdate = guardianRepository.findAll().size();
        guardian.setId(count.incrementAndGet());

        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGuardianMockMvc
            .perform(
                put(ENTITY_API_URL_ID, guardianDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(guardianDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Guardian in the database
        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGuardian() throws Exception {
        int databaseSizeBeforeUpdate = guardianRepository.findAll().size();
        guardian.setId(count.incrementAndGet());

        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGuardianMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(guardianDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Guardian in the database
        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGuardian() throws Exception {
        int databaseSizeBeforeUpdate = guardianRepository.findAll().size();
        guardian.setId(count.incrementAndGet());

        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGuardianMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(guardianDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Guardian in the database
        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGuardianWithPatch() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        int databaseSizeBeforeUpdate = guardianRepository.findAll().size();

        // Update the guardian using partial update
        Guardian partialUpdatedGuardian = new Guardian();
        partialUpdatedGuardian.setId(guardian.getId());

        partialUpdatedGuardian.memberId(UPDATED_MEMBER_ID);

        restGuardianMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGuardian.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGuardian))
            )
            .andExpect(status().isOk());

        // Validate the Guardian in the database
        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeUpdate);
        Guardian testGuardian = guardianList.get(guardianList.size() - 1);
        assertThat(testGuardian.getMemberId()).isEqualTo(UPDATED_MEMBER_ID);
        assertThat(testGuardian.getInvitationSent()).isEqualTo(DEFAULT_INVITATION_SENT);
        assertThat(testGuardian.getInvitationAccepted()).isEqualTo(DEFAULT_INVITATION_ACCEPTED);
    }

    @Test
    @Transactional
    void fullUpdateGuardianWithPatch() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        int databaseSizeBeforeUpdate = guardianRepository.findAll().size();

        // Update the guardian using partial update
        Guardian partialUpdatedGuardian = new Guardian();
        partialUpdatedGuardian.setId(guardian.getId());

        partialUpdatedGuardian
            .memberId(UPDATED_MEMBER_ID)
            .invitationSent(UPDATED_INVITATION_SENT)
            .invitationAccepted(UPDATED_INVITATION_ACCEPTED);

        restGuardianMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGuardian.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGuardian))
            )
            .andExpect(status().isOk());

        // Validate the Guardian in the database
        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeUpdate);
        Guardian testGuardian = guardianList.get(guardianList.size() - 1);
        assertThat(testGuardian.getMemberId()).isEqualTo(UPDATED_MEMBER_ID);
        assertThat(testGuardian.getInvitationSent()).isEqualTo(UPDATED_INVITATION_SENT);
        assertThat(testGuardian.getInvitationAccepted()).isEqualTo(UPDATED_INVITATION_ACCEPTED);
    }

    @Test
    @Transactional
    void patchNonExistingGuardian() throws Exception {
        int databaseSizeBeforeUpdate = guardianRepository.findAll().size();
        guardian.setId(count.incrementAndGet());

        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGuardianMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, guardianDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(guardianDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Guardian in the database
        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGuardian() throws Exception {
        int databaseSizeBeforeUpdate = guardianRepository.findAll().size();
        guardian.setId(count.incrementAndGet());

        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGuardianMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(guardianDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Guardian in the database
        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGuardian() throws Exception {
        int databaseSizeBeforeUpdate = guardianRepository.findAll().size();
        guardian.setId(count.incrementAndGet());

        // Create the Guardian
        GuardianDTO guardianDTO = guardianMapper.toDto(guardian);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGuardianMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(guardianDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Guardian in the database
        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGuardian() throws Exception {
        // Initialize the database
        guardianRepository.saveAndFlush(guardian);

        int databaseSizeBeforeDelete = guardianRepository.findAll().size();

        // Delete the guardian
        restGuardianMockMvc
            .perform(delete(ENTITY_API_URL_ID, guardian.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Guardian> guardianList = guardianRepository.findAll();
        assertThat(guardianList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
