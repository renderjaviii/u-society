package usociety.manager.domain.provider.authentication.impl;

import static org.mockito.ArgumentMatchers.any;
import static usociety.manager.app.api.UserApiFixture.password;
import static usociety.manager.app.api.UserApiFixture.username;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import usociety.manager.app.api.TokenApi;
import usociety.manager.app.api.TokenApiFixture;
import usociety.manager.app.rest.request.LoginRequest;
import usociety.manager.domain.provider.authentication.dto.TokenDTO;
import usociety.manager.domain.provider.web.RestClient;
import usociety.manager.domain.provider.web.RestClientFactory;
import usociety.manager.domain.provider.web.RestClientFactoryBuilder;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationConnectorImplTest {

    private static final Resource KEY_STORE = Mockito.mock(Resource.class);
    private static final String KEY_STORE_PASSWORD = "keyStorePassword";
    private static final String KEY_STORE_TYPE = "keyStoreType";
    private static final String CLIENT_SECRET = "clientSecret";
    private static final String CLIENT_ID = "clientId";
    private static final String AUTH_PATH = "authPath";
    private static final String BASE_URL = "baseURL";

    @Mock
    private RestClientFactory restClientFactory;
    @Mock
    private RestClient restClient;

    private AuthenticationConnectorImpl subject;

    @Before
    public void setUp() {
        Mockito.when(restClientFactory.create(any())).thenReturn(restClient);

        subject = new AuthenticationConnectorImpl(KEY_STORE_PASSWORD,
                CLIENT_SECRET,
                KEY_STORE_TYPE,
                CLIENT_ID,
                AUTH_PATH,
                KEY_STORE,
                BASE_URL,
                restClientFactory);

        ReflectionTestUtils.invokeMethod(subject, "init");
    }

    @Test
    public void shouldMakeLoginCorrectly() {
        Mockito.when(restClient.getToken(any(), any())).thenReturn(TokenDTO.newBuilder()
                .accessToken(TokenApiFixture.accessToken)
                .tokenType(TokenApiFixture.tokenType)
                .refreshToken(TokenApiFixture.refreshToken)
                .expiresIn(TokenApiFixture.expiresIn)
                .scope(TokenApiFixture.scope)
                .jti(TokenApiFixture.jti)
                .build());

        LoginRequest request = new LoginRequest(username, password);
        TokenApi executed = subject.login(request);

        Assert.assertEquals(TokenApiFixture.value(), executed);
        Mockito.verify(restClientFactory).create(RestClientFactoryBuilder.newBuilder()
                .keyStorePassword(KEY_STORE_PASSWORD)
                .keyStoreType(KEY_STORE_TYPE)
                .clientSecret(CLIENT_SECRET)
                .keyStore(KEY_STORE)
                .authPath(AUTH_PATH)
                .clientId(CLIENT_ID)
                .baseURL(BASE_URL)
                .build());

        Mockito.verify(restClient, Mockito.times(1)).setUp();
        Mockito.verify(restClient).getToken(username, password);
    }

}
