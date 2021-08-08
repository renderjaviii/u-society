package usociety.manager.domain.provider.user.impl;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import usociety.manager.app.api.UserApiFixture;
import usociety.manager.app.rest.request.ChangePasswordRequest;
import usociety.manager.app.rest.request.CreateUserRequest;
import usociety.manager.domain.provider.user.dto.UserDTO;
import usociety.manager.domain.provider.web.RestClient;
import usociety.manager.domain.provider.web.RestClientFactory;

@SuppressWarnings( { "unchecked" })
@RunWith(MockitoJUnitRunner.class)
public class UserConnectorImplTest {

    private static final Resource KEY_STORE = Mockito.mock(Resource.class);
    private static final String KEY_STORE_PASSWORD = "keyStorePassword";
    private static final String KEY_STORE_TYPE = "keyStoreType";
    private static final int CONNECTION_TIME_OUT = 123;
    private static final String BASE_URL = "baseURL";
    private static final int READ_TIME_OUT = 98765;
    private static final String PATH = "users";

    @Mock
    private RestClientFactory restClientFactory;

    @Mock
    private RestClient restClient;

    private UserConnectorImpl subject;

    private UserDTO userDTO;

    @Before
    public void setUp() {
        Mockito.when(restClientFactory.create(any())).thenReturn(restClient);

        subject = new UserConnectorImpl(KEY_STORE_PASSWORD,
                CONNECTION_TIME_OUT,
                KEY_STORE_TYPE,
                PATH,
                READ_TIME_OUT,
                BASE_URL,
                KEY_STORE,
                restClientFactory);

        ReflectionTestUtils.invokeMethod(subject, "init");

        userDTO = UserDTO.newBuilder()
                .lastAccessAt(UserApiFixture.lastAccessAt)
                .createdAt(UserApiFixture.createdAt)
                .username(UserApiFixture.username)
                .email(UserApiFixture.email)
                .photo(UserApiFixture.photo)
                .name(UserApiFixture.name)
                .id(UserApiFixture.id)
                .build();
    }

    @Test
    public void shouldCreateUserCorrectly() {
        Mockito.when(restClient.post(any(), any(), ArgumentMatchers.<Class<UserDTO>>notNull())).thenReturn(userDTO);

        CreateUserRequest body = CreateUserRequest.newBuilder().username(UserApiFixture.username).build();
        UserDTO executed = subject.create(body);

        Assert.assertEquals(userDTO, executed);
        ArgumentCaptor<Function<UriBuilder, URI>> uriArgumentCaptor = ArgumentCaptor.forClass(Function.class);
        Mockito.verify(restClient).post(uriArgumentCaptor.capture(), eq(body), eq(UserDTO.class));
        String calledPath = getCalledPath(uriArgumentCaptor);
        Assert.assertEquals(PATH, calledPath);
    }

    @Test
    public void shouldUpdateUserCorrectly() {
        Mockito.when(restClient.patch(any(), any(), ArgumentMatchers.<Class<UserDTO>>notNull())).thenReturn(userDTO);

        UserDTO executed = subject.update(userDTO);

        Assert.assertEquals(userDTO, executed);
        ArgumentCaptor<Function<UriBuilder, URI>> uriArgumentCaptor = ArgumentCaptor.forClass(Function.class);
        Mockito.verify(restClient).patch(uriArgumentCaptor.capture(), eq(userDTO), eq(UserDTO.class));
        String calledPath = getCalledPath(uriArgumentCaptor);
        Assert.assertEquals(PATH, calledPath);
    }

    @Test
    public void shouldGetUserByUserNameCorrectly() {
        Mockito.when(restClient.get(any(), ArgumentMatchers.<Class<UserDTO>>notNull())).thenReturn(userDTO);

        UserDTO executed = subject.get(UserApiFixture.username);

        Assert.assertEquals(userDTO, executed);
        ArgumentCaptor<Function<UriBuilder, URI>> uriArgumentCaptor = ArgumentCaptor.forClass(Function.class);
        Mockito.verify(restClient).get(uriArgumentCaptor.capture(), eq(UserDTO.class));
        String calledPath = getCalledPath(uriArgumentCaptor);
        String expectedPath = String.format("%s/%s", PATH, UserApiFixture.username);
        Assert.assertEquals(expectedPath, calledPath);
    }

    @Test
    public void shouldGetUserByFiltersCorrectly() {
        Mockito.when(restClient.get(any(), ArgumentMatchers.<Class<UserDTO>>notNull())).thenReturn(userDTO);

        UserDTO executed = subject.get(UserApiFixture.id, UserApiFixture.username, UserApiFixture.email);

        Assert.assertEquals(userDTO, executed);
        ArgumentCaptor<Function<UriBuilder, URI>> uriArgumentCaptor = ArgumentCaptor.forClass(Function.class);
        Mockito.verify(restClient).get(uriArgumentCaptor.capture(), eq(UserDTO.class));
        String calledPath = getCalledPath(uriArgumentCaptor);
        String expectedPath = String.format("%s?id=%d&username=%s&email=%s",
                PATH,
                UserApiFixture.id,
                UserApiFixture.username,
                UserApiFixture.email);
        Assert.assertEquals(expectedPath, calledPath);
    }

    @Test
    public void shouldEnableAccountCorrectly() {
        subject.enableAccount(UserApiFixture.username);

        ArgumentCaptor<Function<UriBuilder, URI>> uriArgumentCaptor = ArgumentCaptor.forClass(Function.class);
        Mockito.verify(restClient).post(uriArgumentCaptor.capture(), eq(Void.class));
        String calledPath = getCalledPath(uriArgumentCaptor);
        String expectedPath = String.format("%s/%s/verify-email", PATH, UserApiFixture.username);
        Assert.assertEquals(expectedPath, calledPath);
    }

    @Test
    public void shouldDeleteUserCorrectly() {
        subject.delete(UserApiFixture.username);

        ArgumentCaptor<Function<UriBuilder, URI>> uriArgumentCaptor = ArgumentCaptor.forClass(Function.class);
        Mockito.verify(restClient).delete(uriArgumentCaptor.capture(), eq(Void.class));
        String calledPath = getCalledPath(uriArgumentCaptor);
        String expectedPath = String.format("%s/%s", PATH, UserApiFixture.username);
        Assert.assertEquals(expectedPath, calledPath);
    }

    @Test
    public void shouldGetAllUsersCorrectly() {
        UserDTO tmpUserDTO = UserDTO.newBuilder().id(19L).build();
        Mockito.when(restClient.get(any(), ArgumentMatchers.<ParameterizedTypeReference<List<UserDTO>>>notNull()))
                .thenReturn(Arrays.asList(userDTO, tmpUserDTO));

        List<UserDTO> executed = subject.getAll();

        Assert.assertThat(executed, Matchers.hasSize(2));
        Assert.assertThat(executed, Matchers.containsInAnyOrder(tmpUserDTO, userDTO));
        ArgumentCaptor<Function<UriBuilder, URI>> uriArgumentCaptor = ArgumentCaptor.forClass(Function.class);
        Mockito.verify(restClient).get(uriArgumentCaptor.capture(), eq(new ParameterizedTypeReference<List<UserDTO>>() {
        }));
        String calledPath = getCalledPath(uriArgumentCaptor);
        Assert.assertEquals(PATH.concat("/").concat("all"), calledPath);
    }

    @Test
    public void shouldChangePasswordCorrectly() {
        ChangePasswordRequest body = new ChangePasswordRequest("old", "new");
        subject.changePassword(UserApiFixture.username, body);

        ArgumentCaptor<Function<UriBuilder, URI>> uriArgumentCaptor = ArgumentCaptor.forClass(Function.class);
        Mockito.verify(restClient).patch(uriArgumentCaptor.capture(), eq(body), eq(Void.class));
        String calledPath = getCalledPath(uriArgumentCaptor);
        String expectedPath = String.format("%s/%s/change-password", PATH, UserApiFixture.username);
        Assert.assertEquals(expectedPath, calledPath);
    }

    private String getCalledPath(ArgumentCaptor<Function<UriBuilder, URI>> uriArgumentCaptor) {
        return uriArgumentCaptor.getValue().apply(UriComponentsBuilder.fromPath(EMPTY)).toString();
    }

}