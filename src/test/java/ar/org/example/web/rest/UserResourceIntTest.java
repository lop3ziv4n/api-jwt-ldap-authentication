package ar.org.example.web.rest;

import ar.org.example.ApiJwtLdapAuthenticationApplication;
import ar.org.example.domain.User;
import ar.org.example.repository.UserRepository;
import ar.org.example.service.UserService;
import ar.org.example.service.dto.UserDTO;
import ar.org.example.service.mapper.UserMapper;
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
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiJwtLdapAuthenticationApplication.class)
public class UserResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_SURNAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final Integer DEFAULT_ENABLED = 1;
    private static final Integer UPDATED_ENABLED = 0;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private UserMapper userMapper;


    @Autowired
    private UserService userService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionRestHandler exceptionRestHandler;

    @Autowired
    private EntityManager em;

    private MockMvc restUserMockMvc;

    private User user;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final UserResource userResource = new UserResource(userService);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource)
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
    public static User createEntity(EntityManager em) {
        User user = new User()
                .name(DEFAULT_NAME)
                .surname(DEFAULT_SURNAME)
                .login(DEFAULT_LOGIN)
                .password(DEFAULT_PASSWORD)
                .email(DEFAULT_EMAIL)
                .enabled(DEFAULT_ENABLED);
        return user;
    }

    @Before
    public void initTest() {
        user = createEntity(em);
    }

    @Test
    @Transactional
    public void createUser() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        // Create the User
        UserDTO userDTO = userMapper.toDto(user);
        restUserMockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isCreated());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate + 1);
        User testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUser.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUser.getEnabled()).isEqualTo(DEFAULT_ENABLED);
        assertThat(testUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
    }

    @Test
    @Transactional
    public void createUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        // Create the User with an existing ID
        user.setId(1L);
        UserDTO userDTO = userMapper.toDto(user);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserMockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isBadRequest());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkLoginIsRequired() throws Exception {
        int databaseSizeBeforeTest = userRepository.findAll().size();
        // set the field null
        user.setLogin(null);

        // Create the User, which fails.
        UserDTO userDTO = userMapper.toDto(user);

        restUserMockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isBadRequest());

        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPasswordIsRequired() throws Exception {
        int databaseSizeBeforeTest = userRepository.findAll().size();
        // set the field null
        user.setPassword(null);

        // Create the User, which fails.
        UserDTO userDTO = userMapper.toDto(user);

        restUserMockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isBadRequest());

        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEnabledIsRequired() throws Exception {
        int databaseSizeBeforeTest = userRepository.findAll().size();
        // set the field null
        user.setEnabled(null);

        // Create the User, which fails.
        UserDTO userDTO = userMapper.toDto(user);

        restUserMockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isBadRequest());

        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUsers() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get all the userList
        restUserMockMvc.perform(get("/api/users?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(user.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].surname").value(hasItem(DEFAULT_SURNAME.toString())))
                .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN.toString())))
                .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD.toString())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].enabled").value(hasItem(DEFAULT_ENABLED)));
    }


    @Test
    @Transactional
    public void getUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get the user
        restUserMockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(user.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.surname").value(DEFAULT_SURNAME.toString()))
                .andExpect(jsonPath("$.login").value(DEFAULT_LOGIN.toString()))
                .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD.toString()))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
                .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED));
    }

    @Test
    @Transactional
    public void getUserByLogin() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get the user
        restUserMockMvc.perform(get("/api/users/login/{login}", user.getLogin()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(user.getId().intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.surname").value(DEFAULT_SURNAME.toString()))
                .andExpect(jsonPath("$.login").value(DEFAULT_LOGIN.toString()))
                .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD.toString()))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
                .andExpect(jsonPath("$.enabled").value(DEFAULT_ENABLED));
    }

    @Test
    @Transactional
    public void getNonExistingUser() throws Exception {
        // Get the user
        restUserMockMvc.perform(get("/api/users/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        int databaseSizeBeforeUpdate = userRepository.findAll().size();

        // Update the user
        User updatedUser = userRepository.findById(user.getId()).get();
        // Disconnect from session so that the updates on updatedUser are not directly saved in db
        em.detach(updatedUser);
        updatedUser
                .name(UPDATED_NAME)
                .surname(UPDATED_SURNAME)
                .login(UPDATED_LOGIN)
                .password(UPDATED_PASSWORD)
                .email(UPDATED_EMAIL)
                .enabled(UPDATED_ENABLED);
        UserDTO userDTO = userMapper.toDto(updatedUser);

        restUserMockMvc.perform(put("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isOk());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
        User testUser = userList.get(userList.size() - 1);
        assertThat(testUser.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUser.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testUser.getLogin()).isEqualTo(UPDATED_LOGIN);
        assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUser.getEnabled()).isEqualTo(UPDATED_ENABLED);
        assertThat(testUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    public void updateNonExistingUser() throws Exception {
        int databaseSizeBeforeUpdate = userRepository.findAll().size();

        // Create the User
        UserDTO userDTO = userMapper.toDto(user);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restUserMockMvc.perform(put("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(userDTO)))
                .andExpect(status().isBadRequest());

        // Validate the User in the database
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        int databaseSizeBeforeDelete = userRepository.findAll().size();

        // Get the user
        restUserMockMvc.perform(delete("/api/users/{id}", user.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<User> userList = userRepository.findAll();
        assertThat(userList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(User.class);
        User user = new User();
        user.setId(1L);
        User user1 = new User();
        user1.setId(user.getId());
        assertThat(user).isEqualTo(user1);
        user1.setId(2L);
        assertThat(user).isNotEqualTo(user1);
        user.setId(null);
        assertThat(user).isNotEqualTo(user1);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserDTO.class);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        UserDTO userDTO1 = new UserDTO();
        assertThat(userDTO).isNotEqualTo(userDTO1);
        userDTO1.setId(userDTO.getId());
        assertThat(userDTO).isEqualTo(userDTO1);
        userDTO1.setId(2L);
        assertThat(userDTO).isNotEqualTo(userDTO1);
        userDTO.setId(null);
        assertThat(userDTO).isNotEqualTo(userDTO1);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(userMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(userMapper.fromId(null)).isNull();
    }
}
