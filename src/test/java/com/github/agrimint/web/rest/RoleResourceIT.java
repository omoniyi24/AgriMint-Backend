package com.github.agrimint.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.github.agrimint.IntegrationTest;
import com.github.agrimint.domain.AppUser;
import com.github.agrimint.domain.Privilege;
import com.github.agrimint.domain.Role;
import com.github.agrimint.repository.RoleRepository;
import com.github.agrimint.service.RoleService;
import com.github.agrimint.service.criteria.RoleCriteria;
import com.github.agrimint.service.dto.RoleDTO;
import com.github.agrimint.service.mapper.RoleMapper;
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
 * Integration tests for the {@link RoleResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class RoleResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_USER_DEFINED = false;
    private static final Boolean UPDATED_USER_DEFINED = true;

    private static final Boolean DEFAULT_DEFAULT_ROLE = false;
    private static final Boolean UPDATED_DEFAULT_ROLE = true;

    private static final String DEFAULT_ROLE_GROUP = "AAAAAAAAAA";
    private static final String UPDATED_ROLE_GROUP = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/roles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RoleRepository roleRepository;

    @Mock
    private RoleRepository roleRepositoryMock;

    @Autowired
    private RoleMapper roleMapper;

    @Mock
    private RoleService roleServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRoleMockMvc;

    private Role role;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Role createEntity(EntityManager em) {
        Role role = new Role()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .userDefined(DEFAULT_USER_DEFINED)
            .defaultRole(DEFAULT_DEFAULT_ROLE)
            .roleGroup(DEFAULT_ROLE_GROUP);
        return role;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Role createUpdatedEntity(EntityManager em) {
        Role role = new Role()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .userDefined(UPDATED_USER_DEFINED)
            .defaultRole(UPDATED_DEFAULT_ROLE)
            .roleGroup(UPDATED_ROLE_GROUP);
        return role;
    }

    @BeforeEach
    public void initTest() {
        role = createEntity(em);
    }

    @Test
    @Transactional
    void createRole() throws Exception {
        int databaseSizeBeforeCreate = roleRepository.findAll().size();
        // Create the Role
        RoleDTO roleDTO = roleMapper.toDto(role);
        restRoleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleDTO)))
            .andExpect(status().isCreated());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeCreate + 1);
        Role testRole = roleList.get(roleList.size() - 1);
        assertThat(testRole.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRole.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRole.getUserDefined()).isEqualTo(DEFAULT_USER_DEFINED);
        assertThat(testRole.getDefaultRole()).isEqualTo(DEFAULT_DEFAULT_ROLE);
        assertThat(testRole.getRoleGroup()).isEqualTo(DEFAULT_ROLE_GROUP);
    }

    @Test
    @Transactional
    void createRoleWithExistingId() throws Exception {
        // Create the Role with an existing ID
        role.setId(1L);
        RoleDTO roleDTO = roleMapper.toDto(role);

        int databaseSizeBeforeCreate = roleRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = roleRepository.findAll().size();
        // set the field null
        role.setName(null);

        // Create the Role, which fails.
        RoleDTO roleDTO = roleMapper.toDto(role);

        restRoleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleDTO)))
            .andExpect(status().isBadRequest());

        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = roleRepository.findAll().size();
        // set the field null
        role.setDescription(null);

        // Create the Role, which fails.
        RoleDTO roleDTO = roleMapper.toDto(role);

        restRoleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleDTO)))
            .andExpect(status().isBadRequest());

        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUserDefinedIsRequired() throws Exception {
        int databaseSizeBeforeTest = roleRepository.findAll().size();
        // set the field null
        role.setUserDefined(null);

        // Create the Role, which fails.
        RoleDTO roleDTO = roleMapper.toDto(role);

        restRoleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleDTO)))
            .andExpect(status().isBadRequest());

        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDefaultRoleIsRequired() throws Exception {
        int databaseSizeBeforeTest = roleRepository.findAll().size();
        // set the field null
        role.setDefaultRole(null);

        // Create the Role, which fails.
        RoleDTO roleDTO = roleMapper.toDto(role);

        restRoleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleDTO)))
            .andExpect(status().isBadRequest());

        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllRoles() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList
        restRoleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(role.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].userDefined").value(hasItem(DEFAULT_USER_DEFINED.booleanValue())))
            .andExpect(jsonPath("$.[*].defaultRole").value(hasItem(DEFAULT_DEFAULT_ROLE.booleanValue())))
            .andExpect(jsonPath("$.[*].roleGroup").value(hasItem(DEFAULT_ROLE_GROUP)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRolesWithEagerRelationshipsIsEnabled() throws Exception {
        when(roleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRoleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(roleServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllRolesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(roleServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restRoleMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(roleServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    void getRole() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get the role
        restRoleMockMvc
            .perform(get(ENTITY_API_URL_ID, role.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(role.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.userDefined").value(DEFAULT_USER_DEFINED.booleanValue()))
            .andExpect(jsonPath("$.defaultRole").value(DEFAULT_DEFAULT_ROLE.booleanValue()))
            .andExpect(jsonPath("$.roleGroup").value(DEFAULT_ROLE_GROUP));
    }

    @Test
    @Transactional
    void getRolesByIdFiltering() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        Long id = role.getId();

        defaultRoleShouldBeFound("id.equals=" + id);
        defaultRoleShouldNotBeFound("id.notEquals=" + id);

        defaultRoleShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRoleShouldNotBeFound("id.greaterThan=" + id);

        defaultRoleShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRoleShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllRolesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where name equals to DEFAULT_NAME
        defaultRoleShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the roleList where name equals to UPDATED_NAME
        defaultRoleShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRolesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where name not equals to DEFAULT_NAME
        defaultRoleShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the roleList where name not equals to UPDATED_NAME
        defaultRoleShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRolesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where name in DEFAULT_NAME or UPDATED_NAME
        defaultRoleShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the roleList where name equals to UPDATED_NAME
        defaultRoleShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRolesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where name is not null
        defaultRoleShouldBeFound("name.specified=true");

        // Get all the roleList where name is null
        defaultRoleShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllRolesByNameContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where name contains DEFAULT_NAME
        defaultRoleShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the roleList where name contains UPDATED_NAME
        defaultRoleShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRolesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where name does not contain DEFAULT_NAME
        defaultRoleShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the roleList where name does not contain UPDATED_NAME
        defaultRoleShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllRolesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where description equals to DEFAULT_DESCRIPTION
        defaultRoleShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the roleList where description equals to UPDATED_DESCRIPTION
        defaultRoleShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRolesByDescriptionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where description not equals to DEFAULT_DESCRIPTION
        defaultRoleShouldNotBeFound("description.notEquals=" + DEFAULT_DESCRIPTION);

        // Get all the roleList where description not equals to UPDATED_DESCRIPTION
        defaultRoleShouldBeFound("description.notEquals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRolesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultRoleShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the roleList where description equals to UPDATED_DESCRIPTION
        defaultRoleShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRolesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where description is not null
        defaultRoleShouldBeFound("description.specified=true");

        // Get all the roleList where description is null
        defaultRoleShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllRolesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where description contains DEFAULT_DESCRIPTION
        defaultRoleShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the roleList where description contains UPDATED_DESCRIPTION
        defaultRoleShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRolesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where description does not contain DEFAULT_DESCRIPTION
        defaultRoleShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the roleList where description does not contain UPDATED_DESCRIPTION
        defaultRoleShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllRolesByUserDefinedIsEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where userDefined equals to DEFAULT_USER_DEFINED
        defaultRoleShouldBeFound("userDefined.equals=" + DEFAULT_USER_DEFINED);

        // Get all the roleList where userDefined equals to UPDATED_USER_DEFINED
        defaultRoleShouldNotBeFound("userDefined.equals=" + UPDATED_USER_DEFINED);
    }

    @Test
    @Transactional
    void getAllRolesByUserDefinedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where userDefined not equals to DEFAULT_USER_DEFINED
        defaultRoleShouldNotBeFound("userDefined.notEquals=" + DEFAULT_USER_DEFINED);

        // Get all the roleList where userDefined not equals to UPDATED_USER_DEFINED
        defaultRoleShouldBeFound("userDefined.notEquals=" + UPDATED_USER_DEFINED);
    }

    @Test
    @Transactional
    void getAllRolesByUserDefinedIsInShouldWork() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where userDefined in DEFAULT_USER_DEFINED or UPDATED_USER_DEFINED
        defaultRoleShouldBeFound("userDefined.in=" + DEFAULT_USER_DEFINED + "," + UPDATED_USER_DEFINED);

        // Get all the roleList where userDefined equals to UPDATED_USER_DEFINED
        defaultRoleShouldNotBeFound("userDefined.in=" + UPDATED_USER_DEFINED);
    }

    @Test
    @Transactional
    void getAllRolesByUserDefinedIsNullOrNotNull() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where userDefined is not null
        defaultRoleShouldBeFound("userDefined.specified=true");

        // Get all the roleList where userDefined is null
        defaultRoleShouldNotBeFound("userDefined.specified=false");
    }

    @Test
    @Transactional
    void getAllRolesByDefaultRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where defaultRole equals to DEFAULT_DEFAULT_ROLE
        defaultRoleShouldBeFound("defaultRole.equals=" + DEFAULT_DEFAULT_ROLE);

        // Get all the roleList where defaultRole equals to UPDATED_DEFAULT_ROLE
        defaultRoleShouldNotBeFound("defaultRole.equals=" + UPDATED_DEFAULT_ROLE);
    }

    @Test
    @Transactional
    void getAllRolesByDefaultRoleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where defaultRole not equals to DEFAULT_DEFAULT_ROLE
        defaultRoleShouldNotBeFound("defaultRole.notEquals=" + DEFAULT_DEFAULT_ROLE);

        // Get all the roleList where defaultRole not equals to UPDATED_DEFAULT_ROLE
        defaultRoleShouldBeFound("defaultRole.notEquals=" + UPDATED_DEFAULT_ROLE);
    }

    @Test
    @Transactional
    void getAllRolesByDefaultRoleIsInShouldWork() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where defaultRole in DEFAULT_DEFAULT_ROLE or UPDATED_DEFAULT_ROLE
        defaultRoleShouldBeFound("defaultRole.in=" + DEFAULT_DEFAULT_ROLE + "," + UPDATED_DEFAULT_ROLE);

        // Get all the roleList where defaultRole equals to UPDATED_DEFAULT_ROLE
        defaultRoleShouldNotBeFound("defaultRole.in=" + UPDATED_DEFAULT_ROLE);
    }

    @Test
    @Transactional
    void getAllRolesByDefaultRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where defaultRole is not null
        defaultRoleShouldBeFound("defaultRole.specified=true");

        // Get all the roleList where defaultRole is null
        defaultRoleShouldNotBeFound("defaultRole.specified=false");
    }

    @Test
    @Transactional
    void getAllRolesByRoleGroupIsEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where roleGroup equals to DEFAULT_ROLE_GROUP
        defaultRoleShouldBeFound("roleGroup.equals=" + DEFAULT_ROLE_GROUP);

        // Get all the roleList where roleGroup equals to UPDATED_ROLE_GROUP
        defaultRoleShouldNotBeFound("roleGroup.equals=" + UPDATED_ROLE_GROUP);
    }

    @Test
    @Transactional
    void getAllRolesByRoleGroupIsNotEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where roleGroup not equals to DEFAULT_ROLE_GROUP
        defaultRoleShouldNotBeFound("roleGroup.notEquals=" + DEFAULT_ROLE_GROUP);

        // Get all the roleList where roleGroup not equals to UPDATED_ROLE_GROUP
        defaultRoleShouldBeFound("roleGroup.notEquals=" + UPDATED_ROLE_GROUP);
    }

    @Test
    @Transactional
    void getAllRolesByRoleGroupIsInShouldWork() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where roleGroup in DEFAULT_ROLE_GROUP or UPDATED_ROLE_GROUP
        defaultRoleShouldBeFound("roleGroup.in=" + DEFAULT_ROLE_GROUP + "," + UPDATED_ROLE_GROUP);

        // Get all the roleList where roleGroup equals to UPDATED_ROLE_GROUP
        defaultRoleShouldNotBeFound("roleGroup.in=" + UPDATED_ROLE_GROUP);
    }

    @Test
    @Transactional
    void getAllRolesByRoleGroupIsNullOrNotNull() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where roleGroup is not null
        defaultRoleShouldBeFound("roleGroup.specified=true");

        // Get all the roleList where roleGroup is null
        defaultRoleShouldNotBeFound("roleGroup.specified=false");
    }

    @Test
    @Transactional
    void getAllRolesByRoleGroupContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where roleGroup contains DEFAULT_ROLE_GROUP
        defaultRoleShouldBeFound("roleGroup.contains=" + DEFAULT_ROLE_GROUP);

        // Get all the roleList where roleGroup contains UPDATED_ROLE_GROUP
        defaultRoleShouldNotBeFound("roleGroup.contains=" + UPDATED_ROLE_GROUP);
    }

    @Test
    @Transactional
    void getAllRolesByRoleGroupNotContainsSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList where roleGroup does not contain DEFAULT_ROLE_GROUP
        defaultRoleShouldNotBeFound("roleGroup.doesNotContain=" + DEFAULT_ROLE_GROUP);

        // Get all the roleList where roleGroup does not contain UPDATED_ROLE_GROUP
        defaultRoleShouldBeFound("roleGroup.doesNotContain=" + UPDATED_ROLE_GROUP);
    }

    @Test
    @Transactional
    void getAllRolesByAuthoritiesIsEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);
        Privilege authorities = PrivilegeResourceIT.createEntity(em);
        em.persist(authorities);
        em.flush();
        role.addAuthorities(authorities);
        roleRepository.saveAndFlush(role);
        Long authoritiesId = authorities.getId();

        // Get all the roleList where authorities equals to authoritiesId
        defaultRoleShouldBeFound("authoritiesId.equals=" + authoritiesId);

        // Get all the roleList where authorities equals to (authoritiesId + 1)
        defaultRoleShouldNotBeFound("authoritiesId.equals=" + (authoritiesId + 1));
    }

    @Test
    @Transactional
    void getAllRolesByUsersIsEqualToSomething() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);
        AppUser users = AppUserResourceIT.createEntity(em);
        em.persist(users);
        em.flush();
        role.addUsers(users);
        roleRepository.saveAndFlush(role);
        Long usersId = users.getId();

        // Get all the roleList where users equals to usersId
        defaultRoleShouldBeFound("usersId.equals=" + usersId);

        // Get all the roleList where users equals to (usersId + 1)
        defaultRoleShouldNotBeFound("usersId.equals=" + (usersId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRoleShouldBeFound(String filter) throws Exception {
        restRoleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(role.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].userDefined").value(hasItem(DEFAULT_USER_DEFINED.booleanValue())))
            .andExpect(jsonPath("$.[*].defaultRole").value(hasItem(DEFAULT_DEFAULT_ROLE.booleanValue())))
            .andExpect(jsonPath("$.[*].roleGroup").value(hasItem(DEFAULT_ROLE_GROUP)));

        // Check, that the count call also returns 1
        restRoleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRoleShouldNotBeFound(String filter) throws Exception {
        restRoleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRoleMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingRole() throws Exception {
        // Get the role
        restRoleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewRole() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        int databaseSizeBeforeUpdate = roleRepository.findAll().size();

        // Update the role
        Role updatedRole = roleRepository.findById(role.getId()).get();
        // Disconnect from session so that the updates on updatedRole are not directly saved in db
        em.detach(updatedRole);
        updatedRole
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .userDefined(UPDATED_USER_DEFINED)
            .defaultRole(UPDATED_DEFAULT_ROLE)
            .roleGroup(UPDATED_ROLE_GROUP);
        RoleDTO roleDTO = roleMapper.toDto(updatedRole);

        restRoleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, roleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(roleDTO))
            )
            .andExpect(status().isOk());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
        Role testRole = roleList.get(roleList.size() - 1);
        assertThat(testRole.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRole.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRole.getUserDefined()).isEqualTo(UPDATED_USER_DEFINED);
        assertThat(testRole.getDefaultRole()).isEqualTo(UPDATED_DEFAULT_ROLE);
        assertThat(testRole.getRoleGroup()).isEqualTo(UPDATED_ROLE_GROUP);
    }

    @Test
    @Transactional
    void putNonExistingRole() throws Exception {
        int databaseSizeBeforeUpdate = roleRepository.findAll().size();
        role.setId(count.incrementAndGet());

        // Create the Role
        RoleDTO roleDTO = roleMapper.toDto(role);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, roleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(roleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchRole() throws Exception {
        int databaseSizeBeforeUpdate = roleRepository.findAll().size();
        role.setId(count.incrementAndGet());

        // Create the Role
        RoleDTO roleDTO = roleMapper.toDto(role);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(roleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamRole() throws Exception {
        int databaseSizeBeforeUpdate = roleRepository.findAll().size();
        role.setId(count.incrementAndGet());

        // Create the Role
        RoleDTO roleDTO = roleMapper.toDto(role);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(roleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateRoleWithPatch() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        int databaseSizeBeforeUpdate = roleRepository.findAll().size();

        // Update the role using partial update
        Role partialUpdatedRole = new Role();
        partialUpdatedRole.setId(role.getId());

        partialUpdatedRole
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .defaultRole(UPDATED_DEFAULT_ROLE)
            .roleGroup(UPDATED_ROLE_GROUP);

        restRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRole.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRole))
            )
            .andExpect(status().isOk());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
        Role testRole = roleList.get(roleList.size() - 1);
        assertThat(testRole.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRole.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRole.getUserDefined()).isEqualTo(DEFAULT_USER_DEFINED);
        assertThat(testRole.getDefaultRole()).isEqualTo(UPDATED_DEFAULT_ROLE);
        assertThat(testRole.getRoleGroup()).isEqualTo(UPDATED_ROLE_GROUP);
    }

    @Test
    @Transactional
    void fullUpdateRoleWithPatch() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        int databaseSizeBeforeUpdate = roleRepository.findAll().size();

        // Update the role using partial update
        Role partialUpdatedRole = new Role();
        partialUpdatedRole.setId(role.getId());

        partialUpdatedRole
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .userDefined(UPDATED_USER_DEFINED)
            .defaultRole(UPDATED_DEFAULT_ROLE)
            .roleGroup(UPDATED_ROLE_GROUP);

        restRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedRole.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedRole))
            )
            .andExpect(status().isOk());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
        Role testRole = roleList.get(roleList.size() - 1);
        assertThat(testRole.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRole.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRole.getUserDefined()).isEqualTo(UPDATED_USER_DEFINED);
        assertThat(testRole.getDefaultRole()).isEqualTo(UPDATED_DEFAULT_ROLE);
        assertThat(testRole.getRoleGroup()).isEqualTo(UPDATED_ROLE_GROUP);
    }

    @Test
    @Transactional
    void patchNonExistingRole() throws Exception {
        int databaseSizeBeforeUpdate = roleRepository.findAll().size();
        role.setId(count.incrementAndGet());

        // Create the Role
        RoleDTO roleDTO = roleMapper.toDto(role);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, roleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(roleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchRole() throws Exception {
        int databaseSizeBeforeUpdate = roleRepository.findAll().size();
        role.setId(count.incrementAndGet());

        // Create the Role
        RoleDTO roleDTO = roleMapper.toDto(role);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(roleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamRole() throws Exception {
        int databaseSizeBeforeUpdate = roleRepository.findAll().size();
        role.setId(count.incrementAndGet());

        // Create the Role
        RoleDTO roleDTO = roleMapper.toDto(role);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRoleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(roleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteRole() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        int databaseSizeBeforeDelete = roleRepository.findAll().size();

        // Delete the role
        restRoleMockMvc
            .perform(delete(ENTITY_API_URL_ID, role.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
