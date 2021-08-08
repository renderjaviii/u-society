package usociety.manager.domain.provider.authentication.impl;

import static org.mockito.ArgumentMatchers.any;

import java.net.URI;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import reactor.core.publisher.Mono;
import usociety.manager.app.api.TokenApi;
import usociety.manager.app.api.TokenApiFixture;
import usociety.manager.app.api.UserApiFixture;
import usociety.manager.app.rest.request.LoginRequest;
import usociety.manager.domain.provider.authentication.dto.TokenDTO;
import usociety.manager.domain.provider.web.AbstractConnector;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationConnectorImplTest {

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.Builder builder;
    @Mock
    private WebClient.ResponseSpec responseSpec;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Spy
    private AuthenticationConnectorImpl subject;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(subject, "keyStorePassword", "keyStorePassword");
        ReflectionTestUtils.setField(subject, "clientSecret", "clientSecret");
        ReflectionTestUtils.setField(subject, "keyStoreType", "keyStoreType");
        ReflectionTestUtils.setField(subject, "keyStore", Mockito.mock(Resource.class));
        ReflectionTestUtils.setField(subject, "clientId", "clientId");
        ReflectionTestUtils.setField(subject, "authPath", "authPath");
        ReflectionTestUtils.setField(subject, "baseURL", "baseURL");
        ReflectionTestUtils.setField(subject, "connectionTimeOut", 1);
        ReflectionTestUtils.setField(subject, "readTimeOut", 2);

        //To avoid unnecessary SSL setup
        Mockito.doReturn(webClient).when((AbstractConnector) subject).buildWebClient();
    }

    @Test
    @SuppressWarnings( { "unchecked" })
    public void shouldMakeLoginCorrectly() {
        Mockito.when(webClient.mutate()).thenReturn(builder);
        Mockito.when(builder.defaultHeader(any(), any())).thenReturn(builder);
        Mockito.when(builder.build()).thenReturn(webClient);
        Mockito.when(webClient.post()).thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.uri(ArgumentMatchers.<Function<UriBuilder, URI>>notNull()))
                .thenReturn(requestBodyUriSpec);
        Mockito.when(requestBodyUriSpec.body(any())).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        Mockito.when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(ArgumentMatchers.<Class<TokenDTO>>notNull()))
                .thenReturn(Mono.just(TokenDTO.newBuilder()
                        .accessToken(TokenApiFixture.accessToken)
                        .tokenType(TokenApiFixture.tokenType)
                        .refreshToken(TokenApiFixture.refreshToken)
                        .expiresIn(TokenApiFixture.expiresIn)
                        .scope(TokenApiFixture.scope)
                        .jti(TokenApiFixture.jti)
                        .build()));

        LoginRequest request = new LoginRequest(UserApiFixture.username, UserApiFixture.password);
        TokenApi executed = subject.login(request);
        Assert.assertEquals(TokenApiFixture.defaultValue, executed);
    }

}
