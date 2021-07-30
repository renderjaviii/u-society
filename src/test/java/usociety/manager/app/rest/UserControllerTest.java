package usociety.manager.app.rest;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import usociety.manager.app.handler.RestExceptionHandler;
import usociety.manager.domain.service.user.UserService;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest extends TestUtils {

    @Mock
    private SecurityContext securityContext;
    @Mock
    private UserService userService;

    @Mock
    private UserController subject;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subject)
                .setControllerAdvice(new RestExceptionHandler())
                .build();

        when(securityContext.getAuthentication()).thenReturn(auth2Authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void shouldDeleteUserCorrectly() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/services/users/{username}", USERNAME))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService, Mockito.only()).delete(USERNAME);
    }

    @Test
    public void verify() {
    }

    @Test
    public void enableAccount() {
    }

    @Test
    public void get() {
    }

    @Test
    public void update() {
    }

    @Test
    public void login() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void changePassword() {
    }

}