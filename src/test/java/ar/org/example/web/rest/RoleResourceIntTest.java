package ar.org.example.web.rest;


import ar.org.example.ApiJwtLdapAuthenticationApplication;
import ar.org.example.domain.Role;
import ar.org.example.repository.RoleRepository;
import ar.org.example.service.RoleService;
import ar.org.example.service.dto.RoleDTO;
import ar.org.example.service.mapper.RoleMapper;
import ar.org.example.web.rest.errors.ExceptionRestHandler;
import ar.org.example.web.rest.utils.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static ar.org.example.web.rest.utils.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RoleResource REST controller.
 *
 * @see RoleResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiJwtLdapAuthenticationApplication.class)
public class RoleResourceIntTest {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_ENABLED = 1;
    private static final Integer UPDATED_ENABLED = 0;

    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    private RoleMapper roleMapper;


    @Autowired
    private RoleService roleService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionRestHandler exceptionRestHandler;

    @Autowired
    private EntityManager em;

    private MockMvc restRoleMockMvc;

    private Role role;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RoleResource roleResource = new RoleResource(roleService);
        this.restRoleMockMvc = MockMvcBuilders.standaloneSetup(roleResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(exceptionRestHandler)
                .setConversionService(createFormattingConversionService())
                .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Role createEntity(EntityManager em) {
        Role role = new Role()
                .code(DEFAULT_CODE)
                .description(DEFAULT_DESCRIPTION)
                .enabled(DEFAULT_ENABLED);
        return role;
    }

    @Before
    public void initTest() {
        role = createEntity(em);
    }

    @Test
    @Transactional
    public void createRol() throws Exception {
        int databaseSizeBeforeCreate = roleRepository.findAll().size();

        // Create the Role
        RoleDTO roleDTO = roleMapper.toDto(role);
        restRoleMockMvc.perform(post("/api/roles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(roleDTO)))
                .andExpect(status().isCreated());

        // Validate the Rol in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeCreate + 1);
        Role testRole = roleList.get(roleList.size() - 1);
        assertThat(testRole.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testRole.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRole.getEnabled()).isEqualTo(DEFAULT_ENABLED);
    }

    @Test
    @Transactional
    public void createRolWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = roleRepository.findAll().size();

        // Create the Role with an existing ID
        role.setId(1L);
        RoleDTO roleDTO = roleMapper.toDto(role);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoleMockMvc.perform(post("/api/roles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(roleDTO)))
                .andExpect(status().isBadRequest());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = roleRepository.findAll().size();
        // set the field null
        role.setCode(null);

        // Create the Role, which fails.
        RoleDTO roleDTO = roleMapper.toDto(role);

        restRoleMockMvc.perform(post("/api/roles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(roleDTO)))
                .andExpect(status().isBadRequest());

        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEnabledIsRequired() throws Exception {
        int databaseSizeBeforeTest = roleRepository.findAll().size();
        // set the field null
        role.setEnabled(null);

        // Create the Role, which fails.
        RoleDTO roleDTO = roleMapper.toDto(role);

        restRoleMockMvc.perform(post("/api/roles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(roleDTO)))
                .andExpect(status().isBadRequest());

        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRoles() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get all the roleList
        restRoleMockMvc.perform(get("/api/roles?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
                .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED)));
    }


    @Test
    @Transactional
    public void getRole() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        // Get the role
        restRoleMockMvc.perform(get("/api/roles/{id}", role.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
                .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
                .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED));
    }

    @Test
    @Transactional
    public void getNonExistingRoles() throws Exception {
        // Get the role
        restRoleMockMvc.perform(get("/api/roles/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRole() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        int databaseSizeBeforeUpdate = roleRepository.findAll().size();

        // Update the role
        Role updatedRole = roleRepository.findById(role.getId()).get();
        // Disconnect from session so that the updates on updatedRol are not directly saved in db
        em.detach(updatedRole);
        updatedRole
                .code(UPDATED_CODE)
                .description(UPDATED_DESCRIPTION)
                .enabled(UPDATED_ENABLED);
        RoleDTO roleDTO = roleMapper.toDto(updatedRole);

        restRoleMockMvc.perform(put("/api/roles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(roleDTO)))
                .andExpect(status().isOk());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
        Role testRole = roleList.get(roleList.size() - 1);
        assertThat(testRole.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testRole.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRole.getEnabled()).isEqualTo(UPDATED_ENABLED);
    }

    @Test
    @Transactional
    public void updateNonExistingRole() throws Exception {
        int databaseSizeBeforeUpdate = roleRepository.findAll().size();

        // Create the Role
        RoleDTO roleDTO = roleMapper.toDto(role);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restRoleMockMvc.perform(put("/api/roles")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(roleDTO)))
                .andExpect(status().isBadRequest());

        // Validate the Role in the database
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRole() throws Exception {
        // Initialize the database
        roleRepository.saveAndFlush(role);

        int databaseSizeBeforeDelete = roleRepository.findAll().size();

        // Get the role
        restRoleMockMvc.perform(delete("/api/roles/{id}", role.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Role> roleList = roleRepository.findAll();
        assertThat(roleList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Role.class);
        Role role = new Role();
        role.setId(1L);
        Role role1 = new Role();
        role1.setId(role.getId());
        assertThat(role).isEqualTo(role1);
        role1.setId(2L);
        assertThat(role).isNotEqualTo(role1);
        role.setId(null);
        assertThat(role).isNotEqualTo(role1);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoleDTO.class);
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(1L);
        RoleDTO roleDTO1 = new RoleDTO();
        assertThat(roleDTO).isNotEqualTo(roleDTO1);
        roleDTO1.setId(roleDTO.getId());
        assertThat(roleDTO).isEqualTo(roleDTO1);
        roleDTO1.setId(2L);
        assertThat(roleDTO).isNotEqualTo(roleDTO1);
        roleDTO.setId(null);
        assertThat(roleDTO).isNotEqualTo(roleDTO1);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(roleMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(roleMapper.fromId(null)).isNull();
    }
}
