package common.manager.app.rest;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import common.manager.app.api.ErrorApi;
import common.manager.app.api.UserApi;
import common.manager.app.handler.RestExceptionHandler;
import common.manager.app.rest.request.CreateUserRequest;
import common.manager.domain.exception.GenericException;
import common.manager.domain.service.user.UserService;
import common.manager.domain.util.mapper.CustomObjectMapper;
import common.manager.domain.util.mapper.CustomObjectMapperImpl;

@EnableWebMvc
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    private static final String BASE_URL = "/services/users";

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController subject;

    private CustomObjectMapper objectMapper;

    private MockMvc mockMvc;

    private final Long userId = 123456789L;
    private UserApi user;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(subject)
                .setControllerAdvice(new RestExceptionHandler())
                .build();

        user = UserApi.newBuilder()
                .documentNumber("documentNumber")
                .build();

        objectMapper = new CustomObjectMapperImpl();
    }

    @Test
    public void shouldCreateTheUserUsingTheCorrectData() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateUserRequest.newBuilder()
                        .username("username")
                        .password("password")
                        .documentNumber("docNumber")
                        .userRoles(Collections.singletonList("ROLE"))
                        .build())))
                .andExpect(status().isCreated())
                .andReturn();

        assertEquals(EMPTY, mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void shouldThrowExceptionCreatingUserIfRequestDoesNotHasUsername() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL + "/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(CreateUserRequest.newBuilder()
                        .password("password")
                        .documentNumber("docNumber")
                        .userRoles(Collections.singletonList("ROLE"))
                        .build())))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorApi errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorApi.class);
        assertEquals(ErrorApi.newBuilder()
                        .description("Fields validation failed: username must not be blank")
                        .statusCode("BAD_REQUEST")
                        .build(),
                errorResponse);
    }

    @Test
    public void shouldGetUserUsingTheCorrectData() throws Exception {
        when(userService.get(userId)).thenReturn(user);

        MvcResult mvcResult = mockMvc.perform(get(BASE_URL + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserApi userResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserApi.class);
        objectMapper.writeValueAsString(userResponse);

        assertEquals(user, userResponse);
        verify(userService).get(userId);
    }

    @Test
    public void shouldThrowExceptionGettingUserIfNotExists() throws Exception {
        when(userService.get(userId)).thenThrow(new GenericException("User not exists.", "USER_NOT_EXISTS"));

        MvcResult mvcResult = mockMvc.perform(get(BASE_URL + "/" + userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotAcceptable())
                .andReturn();

        ErrorApi errorResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorApi.class);

        assertEquals(ErrorApi.newBuilder()
                        .description("User not exists.")
                        .statusCode("USER_NOT_EXISTS")
                        .build(),
                errorResponse);
    }

}