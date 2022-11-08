package com.github.agrimint.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.agrimint.IntegrationTest;
import com.github.agrimint.domain.Privilege;
import com.github.agrimint.domain.Role;
import com.github.agrimint.repository.PrivilegeRepository;
import com.github.agrimint.service.criteria.PrivilegeCriteria;
import com.github.agrimint.service.dto.PrivilegeDTO;
import com.github.agrimint.service.mapper.PrivilegeMapper;
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
 * Integration tests for the {@link PrivilegeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PrivilegeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_GROUP = "AAAAAAAAAA";
    private static final String UPDATED_GROUP = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/privileges";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PrivilegeMapper privilegeMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPrivilegeMockMvc;

    private Privilege privilege;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Privilege createEntity(EntityManager em) {
        Privilege privilege = new Privilege().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).group(DEFAULT_GROUP);
        return privilege;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Privilege createUpdatedEntity(EntityManager em) {
        Privilege privilege = new Privilege().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).group(UPDATED_GROUP);
        return privilege;
    }

    @BeforeEach
    public void initTest() {
        privilege = createEntity(em);
    }

    @Test
    @Transactional
    void createPrivilege() throws Exception {
        int databaseSizeBeforeCreate = privilegeRepository.findAll().size();
        // Create the Privilege
        PrivilegeDTO privilegeDTO = privilegeMapper.toDto(privilege);
        restPrivilegeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(privilegeDTO)))
            .andExpect(status().isCreated());

        // Validate the Privilege in the database
        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeCreate + 1);
        Privilege testPrivilege = privilegeList.get(privilegeList.size() - 1);
        assertThat(testPrivilege.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testPrivilege.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testPrivilege.getGroup()).isEqualTo(DEFAULT_GROUP);
    }

    @Test
    @Transactional
    void createPrivilegeWithExistingId() throws Exception {
        // Create the Privilege with an existing ID
        privilege.setId(1L);
        PrivilegeDTO privilegeDTO = privilegeMapper.toDto(privilege);

        int databaseSizeBeforeCreate = privilegeRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPrivilegeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(privilegeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Privilege in the database
        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = privilegeRepository.findAll().size();
        // set the field null
        privilege.setName(null);

        // Create the Privilege, which fails.
        PrivilegeDTO privilegeDTO = privilegeMapper.toDto(privilege);

        restPrivilegeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(privilegeDTO)))
            .andExpect(status().isBadRequest());

        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = privilegeRepository.findAll().size();
        // set the field null
        privilege.setDescription(null);

        // Create the Privilege, which fails.
        PrivilegeDTO privilegeDTO = privilegeMapper.toDto(privilege);

        restPrivilegeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(privilegeDTO)))
            .andExpect(status().isBadRequest());

        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPrivileges() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList
        restPrivilegeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(privilege.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].group").value(hasItem(DEFAULT_GROUP)));
    }

    @Test
    @Transactional
    void getPrivilege() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get the privilege
        restPrivilegeMockMvc
            .perform(get(ENTITY_API_URL_ID, privilege.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(privilege.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.group").value(DEFAULT_GROUP));
    }

    @Test
    @Transactional
    void getPrivilegesByIdFiltering() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        Long id = privilege.getId();

        defaultPrivilegeShouldBeFound("id.equals=" + id);
        defaultPrivilegeShouldNotBeFound("id.notEquals=" + id);

        defaultPrivilegeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPrivilegeShouldNotBeFound("id.greaterThan=" + id);

        defaultPrivilegeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPrivilegeShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllPrivilegesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where name equals to DEFAULT_NAME
        defaultPrivilegeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the privilegeList where name equals to UPDATED_NAME
        defaultPrivilegeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPrivilegesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where name not equals to DEFAULT_NAME
        defaultPrivilegeShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the privilegeList where name not equals to UPDATED_NAME
        defaultPrivilegeShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPrivilegesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPrivilegeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the privilegeList where name equals to UPDATED_NAME
        defaultPrivilegeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPrivilegesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where name is not null
        defaultPrivilegeShouldBeFound("name.specified=true");

        // Get all the privilegeList where name is null
        defaultPrivilegeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllPrivilegesByNameContainsSomething() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where name contains DEFAULT_NAME
        defaultPrivilegeShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the privilegeList where name contains UPDATED_NAME
        defaultPrivilegeShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPrivilegesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where name does not contain DEFAULT_NAME
        defaultPrivilegeShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the privilegeList where name does not contain UPDATED_NAME
        defaultPrivilegeShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllPrivilegesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where description equals to DEFAULT_DESCRIPTION
        defaultPrivilegeShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the privilegeList where description equals to UPDATED_DESCRIPTION
        defaultPrivilegeShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPrivilegesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where description not equals to DEFAULT_DESCRIPTION
        defaultPrivilegeShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the privilegeList where description not equals to UPDATED_DESCRIPTION
        defaultPrivilegeShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPrivilegesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultPrivilegeShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the privilegeList where description equals to UPDATED_DESCRIPTION
        defaultPrivilegeShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPrivilegesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where description is not null
        defaultPrivilegeShouldBeFound("description.specified=true");

        // Get all the privilegeList where description is null
        defaultPrivilegeShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllPrivilegesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where description contains DEFAULT_DESCRIPTION
        defaultPrivilegeShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the privilegeList where description contains UPDATED_DESCRIPTION
        defaultPrivilegeShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPrivilegesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where description does not contain DEFAULT_DESCRIPTION
        defaultPrivilegeShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the privilegeList where description does not contain UPDATED_DESCRIPTION
        defaultPrivilegeShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllPrivilegesByGroupIsEqualToSomething() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where group equals to DEFAULT_GROUP
        defaultPrivilegeShouldBeFound("group.equals=" + DEFAULT_GROUP);

        // Get all the privilegeList where group equals to UPDATED_GROUP
        defaultPrivilegeShouldNotBeFound("group.equals=" + UPDATED_GROUP);
    }

    @Test
    @Transactional
    void getAllPrivilegesByGroupIsNotEqualToSomething() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where group not equals to DEFAULT_GROUP
        defaultPrivilegeShouldNotBeFound("group.notEquals=" + DEFAULT_GROUP);

        // Get all the privilegeList where group not equals to UPDATED_GROUP
        defaultPrivilegeShouldBeFound("group.notEquals=" + UPDATED_GROUP);
    }

    @Test
    @Transactional
    void getAllPrivilegesByGroupIsInShouldWork() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where group in DEFAULT_GROUP or UPDATED_GROUP
        defaultPrivilegeShouldBeFound("group.in=" + DEFAULT_GROUP + "," + UPDATED_GROUP);

        // Get all the privilegeList where group equals to UPDATED_GROUP
        defaultPrivilegeShouldNotBeFound("group.in=" + UPDATED_GROUP);
    }

    @Test
    @Transactional
    void getAllPrivilegesByGroupIsNullOrNotNull() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where group is not null
        defaultPrivilegeShouldBeFound("group.specified=true");

        // Get all the privilegeList where group is null
        defaultPrivilegeShouldNotBeFound("group.specified=false");
    }

    @Test
    @Transactional
    void getAllPrivilegesByGroupContainsSomething() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where group contains DEFAULT_GROUP
        defaultPrivilegeShouldBeFound("group.contains=" + DEFAULT_GROUP);

        // Get all the privilegeList where group contains UPDATED_GROUP
        defaultPrivilegeShouldNotBeFound("group.contains=" + UPDATED_GROUP);
    }

    @Test
    @Transactional
    void getAllPrivilegesByGroupNotContainsSomething() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        // Get all the privilegeList where group does not contain DEFAULT_GROUP
        defaultPrivilegeShouldNotBeFound("group.doesNotContain=" + DEFAULT_GROUP);

        // Get all the privilegeList where group does not contain UPDATED_GROUP
        defaultPrivilegeShouldBeFound("group.doesNotContain=" + UPDATED_GROUP);
    }

    @Test
    @Transactional
    void getAllPrivilegesByRolesIsEqualToSomething() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);
        Role roles = RoleResourceIT.createEntity(em);
        em.persist(roles);
        em.flush();
        privilege.addRoles(roles);
        privilegeRepository.saveAndFlush(privilege);
        Long rolesId = roles.getId();

        // Get all the privilegeList where roles equals to rolesId
        defaultPrivilegeShouldBeFound("rolesId.equals=" + rolesId);

        // Get all the privilegeList where roles equals to (rolesId + 1)
        defaultPrivilegeShouldNotBeFound("rolesId.equals=" + (rolesId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPrivilegeShouldBeFound(String filter) throws Exception {
        restPrivilegeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(privilege.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].group").value(hasItem(DEFAULT_GROUP)));

        // Check, that the count call also returns 1
        restPrivilegeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPrivilegeShouldNotBeFound(String filter) throws Exception {
        restPrivilegeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPrivilegeMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingPrivilege() throws Exception {
        // Get the privilege
        restPrivilegeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPrivilege() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        int databaseSizeBeforeUpdate = privilegeRepository.findAll().size();

        // Update the privilege
        Privilege updatedPrivilege = privilegeRepository.findById(privilege.getId()).get();
        // Disconnect from session so that the updates on updatedPrivilege are not directly saved in db
        em.detach(updatedPrivilege);
        updatedPrivilege.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).group(UPDATED_GROUP);
        PrivilegeDTO privilegeDTO = privilegeMapper.toDto(updatedPrivilege);

        restPrivilegeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, privilegeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(privilegeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Privilege in the database
        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeUpdate);
        Privilege testPrivilege = privilegeList.get(privilegeList.size() - 1);
        assertThat(testPrivilege.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPrivilege.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPrivilege.getGroup()).isEqualTo(UPDATED_GROUP);
    }

    @Test
    @Transactional
    void putNonExistingPrivilege() throws Exception {
        int databaseSizeBeforeUpdate = privilegeRepository.findAll().size();
        privilege.setId(count.incrementAndGet());

        // Create the Privilege
        PrivilegeDTO privilegeDTO = privilegeMapper.toDto(privilege);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrivilegeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, privilegeDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(privilegeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Privilege in the database
        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPrivilege() throws Exception {
        int databaseSizeBeforeUpdate = privilegeRepository.findAll().size();
        privilege.setId(count.incrementAndGet());

        // Create the Privilege
        PrivilegeDTO privilegeDTO = privilegeMapper.toDto(privilege);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrivilegeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(privilegeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Privilege in the database
        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPrivilege() throws Exception {
        int databaseSizeBeforeUpdate = privilegeRepository.findAll().size();
        privilege.setId(count.incrementAndGet());

        // Create the Privilege
        PrivilegeDTO privilegeDTO = privilegeMapper.toDto(privilege);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrivilegeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(privilegeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Privilege in the database
        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePrivilegeWithPatch() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        int databaseSizeBeforeUpdate = privilegeRepository.findAll().size();

        // Update the privilege using partial update
        Privilege partialUpdatedPrivilege = new Privilege();
        partialUpdatedPrivilege.setId(privilege.getId());

        partialUpdatedPrivilege.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restPrivilegeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrivilege.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPrivilege))
            )
            .andExpect(status().isOk());

        // Validate the Privilege in the database
        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeUpdate);
        Privilege testPrivilege = privilegeList.get(privilegeList.size() - 1);
        assertThat(testPrivilege.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPrivilege.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPrivilege.getGroup()).isEqualTo(DEFAULT_GROUP);
    }

    @Test
    @Transactional
    void fullUpdatePrivilegeWithPatch() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        int databaseSizeBeforeUpdate = privilegeRepository.findAll().size();

        // Update the privilege using partial update
        Privilege partialUpdatedPrivilege = new Privilege();
        partialUpdatedPrivilege.setId(privilege.getId());

        partialUpdatedPrivilege.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).group(UPDATED_GROUP);

        restPrivilegeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPrivilege.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPrivilege))
            )
            .andExpect(status().isOk());

        // Validate the Privilege in the database
        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeUpdate);
        Privilege testPrivilege = privilegeList.get(privilegeList.size() - 1);
        assertThat(testPrivilege.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPrivilege.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPrivilege.getGroup()).isEqualTo(UPDATED_GROUP);
    }

    @Test
    @Transactional
    void patchNonExistingPrivilege() throws Exception {
        int databaseSizeBeforeUpdate = privilegeRepository.findAll().size();
        privilege.setId(count.incrementAndGet());

        // Create the Privilege
        PrivilegeDTO privilegeDTO = privilegeMapper.toDto(privilege);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPrivilegeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, privilegeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(privilegeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Privilege in the database
        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPrivilege() throws Exception {
        int databaseSizeBeforeUpdate = privilegeRepository.findAll().size();
        privilege.setId(count.incrementAndGet());

        // Create the Privilege
        PrivilegeDTO privilegeDTO = privilegeMapper.toDto(privilege);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrivilegeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(privilegeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Privilege in the database
        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPrivilege() throws Exception {
        int databaseSizeBeforeUpdate = privilegeRepository.findAll().size();
        privilege.setId(count.incrementAndGet());

        // Create the Privilege
        PrivilegeDTO privilegeDTO = privilegeMapper.toDto(privilege);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPrivilegeMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(privilegeDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Privilege in the database
        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePrivilege() throws Exception {
        // Initialize the database
        privilegeRepository.saveAndFlush(privilege);

        int databaseSizeBeforeDelete = privilegeRepository.findAll().size();

        // Delete the privilege
        restPrivilegeMockMvc
            .perform(delete(ENTITY_API_URL_ID, privilege.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Privilege> privilegeList = privilegeRepository.findAll();
        assertThat(privilegeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
