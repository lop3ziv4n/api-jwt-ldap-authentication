package ar.org.example.web.rest;

import ar.org.example.ApiJwtLdapAuthenticationApplication;
import ar.org.example.security.jwt.TokenProvider;
import ar.org.example.service.UserAuthenticationService;
import ar.org.example.web.rest.errors.ExceptionRestHandler;
import ar.org.example.web.rest.utils.TestUtil;
import ar.org.example.web.rest.vm.LoginVM;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the UserAuthenticationResource REST controller.
 *
 * @see UserAuthenticationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiJwtLdapAuthenticationApplication.class)
public class UserAuthenticationResourceIntTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private ExceptionRestHandler exceptionRestHandler;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        UserAuthenticationResource userAuthenticationResource = new UserAuthenticationResource(tokenProvider, authenticationManager, userAuthenticationService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userAuthenticationResource)
                .setControllerAdvice(exceptionRestHandler)
                .build();
    }

    @Test
    @Transactional
    public void testAuthorize() throws Exception {
        LoginVM login = new LoginVM();
        login.setUsername("prueba1");
        login.setPassword("Marzo2013");
        mockMvc.perform(post("/api/authenticate")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(header().string("Authorization", not(nullValue())))
                .andExpect(header().string("Authorization", not(isEmptyString())));
    }

    @Test
    @Transactional
    public void testAuthorizeWithRememberMe() throws Exception {
        LoginVM login = new LoginVM();
        login.setUsername("prueba1");
        login.setPassword("Marzo2013");
        login.setRememberMe(true);
        mockMvc.perform(post("/api/authenticate")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(header().string("Authorization", not(nullValue())))
                .andExpect(header().string("Authorization", not(isEmptyString())));
    }

    @Test
    @Transactional
    public void testAuthorizeFails() throws Exception {
        LoginVM login = new LoginVM();
        login.setUsername("wrong-user");
        login.setPassword("wrong password");
        mockMvc.perform(post("/api/authenticate")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(header().doesNotExist("Authorization"));
    }
}
