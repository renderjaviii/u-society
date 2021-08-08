package usociety.manager.domain.provider.web.impl;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyStore;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

import javax.net.ssl.TrustManagerFactory;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import usociety.manager.app.api.ApiError;
import usociety.manager.domain.exception.WebException;
import usociety.manager.domain.provider.authentication.dto.TokenDTO;
import usociety.manager.domain.provider.web.RestClient;
import usociety.manager.domain.provider.web.RestClientFactoryBuilder;

public class ReactiveRestClientImpl implements RestClient {

    private static final String BUILDING_CLIENT_ERROR_CODE = "ERROR_SETTING_REST_CLIENT";

    private static final int DEFAULT_CONNECTION_TIME_OUT = 5000;
    private static final int DEFAULT_READ_TIME_OUT = 30000;

    private final Integer connectionTimeOut;
    private final String keyStorePassword;
    private final String clientSecret;
    private final String keyStoreType;
    private final Integer readTimeOut;
    private final Resource keyStore;
    private final String clientId;
    private final String authPath;
    private final String baseURL;

    private WebClient webClient;

    public ReactiveRestClientImpl(RestClientFactoryBuilder builder) {
        Assert.notNull(builder.getBaseURL(), "baseURL cannot be null");
        connectionTimeOut = getOrDefault(builder.getConnectionTimeOut(), DEFAULT_CONNECTION_TIME_OUT);
        readTimeOut = getOrDefault(builder.getReadTimeOut(), DEFAULT_READ_TIME_OUT);
        keyStorePassword = builder.getKeyStorePassword();
        keyStoreType = builder.getKeyStoreType();
        clientSecret = builder.getClientSecret();
        keyStore = builder.getKeyStore();
        authPath = builder.getAuthPath();
        clientId = builder.getClientId();
        baseURL = builder.getBaseURL();
    }

    @Override
    public void setUp() {
        webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(buildHttpClient()))
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .filter(responseFilter())
                .baseUrl(baseURL)
                .build();
    }

    @Override
    public TokenDTO getToken(String username, String password) {
        validateSetUp();
        return webClient.mutate()
                .defaultHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(authPath)
                        .queryParam(OAuth2Utils.GRANT_TYPE, "password")
                        .build())
                .body(BodyInserters.fromFormData("username", username).with("password", password))
                .headers(httpHeaders -> httpHeaders.setBasicAuth(clientId, clientSecret))
                .retrieve()
                .bodyToMono(TokenDTO.class)
                .block();
    }

    @Override
    public TokenDTO getToken() {
        validateSetUp();
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(authPath)
                        .queryParam(OAuth2Utils.GRANT_TYPE, "client_credentials")
                        .build())
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .retrieve()
                .bodyToMono(TokenDTO.class)
                .block();
    }

    @Override
    public <T> T get(Function<UriBuilder, URI> uriFunction, Class<T> responseClazz) {
        validateSetUp();
        return webClient.get()
                .uri(uriFunction)
                .retrieve()
                .bodyToMono(responseClazz)
                .block();
    }

    @Override
    public <T> T get(Function<UriBuilder, URI> uriFunction, ParameterizedTypeReference<T> typeReference) {
        validateSetUp();
        return webClient.get()
                .uri(uriFunction)
                .retrieve()
                .bodyToMono(typeReference)
                .block();
    }

    @Override
    public <T> T post(Function<UriBuilder, URI> uriFunction, Class<T> responseClazz) {
        validateSetUp();
        return post(uriFunction, EMPTY, responseClazz);
    }

    @Override
    public <T> T post(Function<UriBuilder, URI> uriFunction, Object body, Class<T> responseClazz) {
        validateSetUp();
        return webClient.post()
                .uri(uriFunction)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseClazz)
                .block();
    }

    @Override
    public <T> T post(Function<UriBuilder, URI> uriFunction, Object body, ParameterizedTypeReference<T> typeReference) {
        validateSetUp();
        return webClient.post()
                .uri(uriFunction)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(typeReference)
                .block();
    }

    @Override
    public <T> T put(Function<UriBuilder, URI> uriFunction, Class<T> responseClazz) {
        validateSetUp();
        return put(uriFunction, EMPTY, responseClazz);
    }

    @Override
    public <T> T put(Function<UriBuilder, URI> uriFunction, Object body, Class<T> responseClazz) {
        validateSetUp();
        return webClient.put()
                .uri(uriFunction)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseClazz)
                .block();
    }

    @Override
    public <T> T patch(Function<UriBuilder, URI> uriFunction, Class<T> responseClazz) {
        validateSetUp();
        return patch(uriFunction, EMPTY, responseClazz);
    }

    @Override
    public <T> T patch(Function<UriBuilder, URI> uriFunction, Object body, Class<T> responseClazz) {
        validateSetUp();
        return webClient.patch()
                .uri(uriFunction)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseClazz)
                .block();
    }

    @Override
    public <T> T delete(Function<UriBuilder, URI> uriFunction, Class<T> responseClazz) {
        validateSetUp();
        return webClient.delete()
                .uri(uriFunction)
                .retrieve()
                .bodyToMono(responseClazz)
                .block();
    }

    private HttpClient buildHttpClient() {
        return HttpClient.create()
                .tcpConfiguration(tcpClient -> tcpClient
                        .option(CONNECT_TIMEOUT_MILLIS, connectionTimeOut)
                        .doOnConnected(connection -> connection
                                .addHandlerLast(new ReadTimeoutHandler(readTimeOut, MILLISECONDS))))
                .secure(sslContextSpec -> sslContextSpec
                        .sslContext(getSSLContext())
                        .handshakeTimeout(Duration.ofMillis(connectionTimeOut)));
    }

    private SslContext getSSLContext() {
        try {
            Path truststorePath = Paths.get(keyStore.getURI());
            InputStream truststoreInputStream = Files.newInputStream(truststorePath, StandardOpenOption.READ);

            KeyStore truststore = KeyStore.getInstance(keyStoreType);
            truststore.load(truststoreInputStream, keyStorePassword.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(truststore);

            return SslContextBuilder.forClient().trustManager(trustManagerFactory).build();
        } catch (Exception e) {
            throw new WebException("Error setting SSL context", BUILDING_CLIENT_ERROR_CODE, e);
        }
    }

    private ExchangeFilterFunction responseFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (response.statusCode().isError()) {
                return response
                        .bodyToMono(ApiError.class)
                        .flatMap(error -> Mono.error(new WebException(error)));
            }
            return Mono.just(response);
        });
    }

    public <T> T getOrDefault(T value, T defaultValue) {
        return Objects.isNull(value) ? defaultValue : value;
    }

    private void validateSetUp() {
        if (Objects.isNull(webClient)) {
            throw new UnsupportedOperationException("init method must be invoked");
        }
    }

}

