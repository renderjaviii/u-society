package usociety.manager.domain.provider.web.impl;

public class ReactiveRestClientImplTest {

    /*
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

        LoginRequest request = new LoginRequest(username, password);
        TokenApi executed = subject.login(request);

        Assert.assertEquals(TokenApiFixture.value(), executed);

        //Only for testing, when ReactiveConnector is loaded on Nexus, It should be removed.
        Mockito.verify(builder).defaultHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE);

        ArgumentCaptor<BodyInserter<?, ? super ClientHttpRequest>> bodyInserterArgumentCaptor = ArgumentCaptor
                .forClass(BodyInserter.class);
        Mockito.verify(requestBodyUriSpec).body(bodyInserterArgumentCaptor.capture());
        BodyInserter<?, ? super ClientHttpRequest> capturedValue = bodyInserterArgumentCaptor.getValue();
        LinkedMultiValueMap<String, LinkedList<String>> insertedValues
                = (LinkedMultiValueMap<String, LinkedList<String>>) ReflectionTestUtils
                .getField(capturedValue, "data");
        Assert.assertThat(insertedValues, IsMapContaining.hasEntry("username", Collections.singletonList(username)));
        Assert.assertThat(insertedValues, IsMapContaining.hasEntry("password", Collections.singletonList(password)));

        ArgumentCaptor<Consumer<HttpHeaders>> consumerArgumentCaptor = ArgumentCaptor.forClass(Consumer.class);
        Mockito.verify(requestHeadersSpec).headers(consumerArgumentCaptor.capture());
        Consumer<HttpHeaders> capturedHeaders = consumerArgumentCaptor.getValue();
        Assert.assertEquals("clientId", ReflectionTestUtils.getField(capturedHeaders, "arg$1"));
        Assert.assertEquals("clientSecret", ReflectionTestUtils.getField(capturedHeaders, "arg$2"));
  RestClient executed = subject.create(RestClientFactoryBuilder.newBuilder()
                .keyStorePassword("keyStorePassword")
                .clientSecret("clientSecret")
                .keyStoreType("keyStoreType")
                .connectionTimeOut(12345)
                .readTimeOut(98765)
                .clientId("clientId")
                .authPath("authPath")
                .baseURL("baseURL")
                .clock(clock)
                .build());

     */
}