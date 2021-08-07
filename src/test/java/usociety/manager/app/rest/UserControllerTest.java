package usociety.manager.app.rest;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.when;
import static usociety.manager.domain.util.Constants.INVALID_CREDENTIALS;

import java.util.Collections;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import usociety.manager.TestUtils;
import usociety.manager.app.api.ApiError;
import usociety.manager.app.api.TokenApiFixture;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.api.UserApiFixture;
import usociety.manager.app.handler.RestExceptionHandler;
import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.app.rest.request.LoginRequest;
import usociety.manager.app.rest.request.UpdateUserRequest;
import usociety.manager.app.rest.response.LoginResponse;
import usociety.manager.domain.service.user.UserService;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest extends TestUtils {

    private static final String EMAIL = "email@domain.com";

    @Mock
    private SecurityContext securityContext;
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController subject;

    private MockMvc mockMvc;

    private LoginResponse loginResponse;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subject)
                .setControllerAdvice(new RestExceptionHandler())
                .build();

        when(securityContext.getAuthentication()).thenReturn(auth2Authentication);
        SecurityContextHolder.setContext(securityContext);

        loginResponse = new LoginResponse(UserApiFixture.defaultValue, TokenApiFixture.defaultValue);
    }

    @Test
    public void shouldChangePasswordCorrectly() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("1234ABC", "T3$t123abc");
        String otpCode = "2468";

        mockMvc.perform(MockMvcRequestBuilders.patch("/services/users/{username}/change-password", USERNAME)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
                .param("otpCode", otpCode))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService).changePassword(USERNAME, otpCode, request);
    }

    @Test
    public void shouldDeleteUserCorrectly() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/services/users/{username}", USERNAME))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService).delete(USERNAME);
    }

    @Test
    public void shouldUpdateUserCorrectly() throws Exception {
        Mockito.when(userService.update(any(), any())).thenReturn(UserApiFixture.defaultValue);

        UpdateUserRequest request = new UpdateUserRequest("New name", EMPTY, Collections.emptySet());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/services/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserApi executed = readMvcResultValue(mvcResult, UserApi.class);

        Assert.assertEquals(UserApiFixture.defaultValue, executed);
        Mockito.verify(userService).update(USERNAME, request);
    }

    @Test
    public void shouldMakeLoginCorrectly() throws Exception {
        Mockito.when(userService.login(any())).thenReturn(loginResponse);

        LoginRequest request = new LoginRequest(UserApiFixture.username, "12345ABC");
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/services/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        LoginResponse executed = readMvcResultValue(mvcResult, LoginResponse.class);

        Assert.assertEquals(loginResponse, executed);
        Mockito.verify(userService).login(request);
    }

    @Test
    public void shouldVerifyEmailCorrectly() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/services/users/{email}/verify", EMAIL))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService).verify(EMAIL);
    }

    @Test
    public void shouldCreateUserCorrectly() throws Exception {
        Mockito.when(userService.create(any())).thenReturn(loginResponse);

        CreateUserRequest request = CreateUserRequest.newBuilder()
                .username(UserApiFixture.username)
                .photo(UserApiFixture.photo)
                .email(UserApiFixture.email)
                .name(UserApiFixture.name)
                .password("T3$t123abc")
                .otpCode("12345")
                .build();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/services/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        LoginResponse executed = readMvcResultValue(mvcResult, LoginResponse.class);

        Assert.assertEquals(loginResponse, executed);
        Mockito.verify(userService, only()).create(request);
    }

    @Test
    public void shouldEnableAccountCorrectly() throws Exception {
        String otpCode = "98765";
        mockMvc.perform(MockMvcRequestBuilders.post("/services/users/{email}/enable-account", EMAIL)
                .param("otpCode", otpCode))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService).enableAccount(EMAIL, otpCode);
    }

    @Test
    public void shouldGetUserByUsernameCorrectly() throws Exception {
        Mockito.when(userService.get(Mockito.any())).thenReturn(UserApiFixture.defaultValue);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/services/users/" + USERNAME))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserApi executed = readMvcResultValue(mvcResult, UserApi.class);

        Assert.assertEquals(UserApiFixture.defaultValue, executed);
        Mockito.verify(userService).get(USERNAME);
    }

    @Test
    public void shouldFailDeletingUserIfTokenDoesNotBelongToHim() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .delete("/services/users/{username}", "fake-username"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();

        ApiError executed = readMvcResultValue(mvcResult, ApiError.class);

        Assert.assertEquals(INVALID_CREDENTIALS, executed.getStatusCode());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    public void shouldFailChangingUserPasswordIfTokenDoesNotBelongToHim() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("1234ABC", "T3$t123abc");
        String otpCode = "2468";

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .patch("/services/users/{username}/change-password", "fake-username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request))
                .param("otpCode", otpCode))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();

        ApiError executed = readMvcResultValue(mvcResult, ApiError.class);

        Assert.assertEquals(INVALID_CREDENTIALS, executed.getStatusCode());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    public void shouldFailCreatingUserIfRequestIsInvalid() throws Exception {
        CreateUserRequest request = CreateUserRequest.newBuilder()
                .username("invalid$user?")
                .email("example.com")
                .build();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/services/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        ApiError executed = readMvcResultValue(mvcResult, ApiError.class);

        String errorDescription = executed.getDescription();
        Assert.assertThat(errorDescription, Matchers.containsString("email must be a well-formed email address"));
        Assert.assertThat(errorDescription, Matchers.containsString("username must match \"^[a-zA-Z\\d_.]+\""));
        Assert.assertThat(errorDescription, Matchers.containsString("name must not be blank"));
        Assert.assertThat(errorDescription, Matchers.containsString("password must not be blank"));

        Mockito.verifyNoInteractions(userService);
    }

}
