package common.manager.domain.service.web;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.net.URI;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import common.manager.app.api.TokenApi;
import common.manager.domain.exception.WebException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.internal.StringUtil;
import reactor.netty.http.client.HttpClient;

public class WebClientService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final int TIME_OUT = 30;

    private WebClient webClient;

    private String baseUrl;
    private String authUri;
    private TokenApi token;

    protected void setUp(String url, int timeOut) {
        logger.info("Creating WebClient for host: {}", url);
        if (StringUtil.isNullOrEmpty(url)) {
            throw new UnsupportedOperationException("The base url is invalid.");
        }
        init(url, timeOut, EMPTY);
    }

    protected void setUp(String url, int timeOut, String authUrl) {
        logger.info("Creating WebClient for host: {}", url);
        if (StringUtil.isNullOrEmpty(url)) {
            throw new UnsupportedOperationException("The base url is invalid.");
        }
        if (StringUtil.isNullOrEmpty(authUrl)) {
            throw new UnsupportedOperationException("The auth url is invalid.");
        }

        init(url, timeOut, authUrl);
    }

    private void init(String url, int timeOut, String authUrl) {
        baseUrl = url;
        authUri = authUrl;
        if (timeOut <= 0) {
            timeOut = TIME_OUT;
        }
        buildWebClient(timeOut);
    }

    private void buildWebClient(int timeOut) {
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                        .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(timeOut))));

        webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient.wiretap(true)))
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build();
    }

    protected void getTokenUsingPassword(String clientId,
                                         String clientSecret,
                                         String username,
                                         String password) {
        token = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .build()
                .post().uri(uriBuilder()
                        .path(authUri)
                        .queryParam("grant_type", "password")
                        .build().toString())
                .body(BodyInserters.fromFormData("username", username).with("password", password))
                .headers(httpHeaders -> httpHeaders.setBasicAuth(clientId, clientSecret))
                .retrieve()
                .bodyToMono(TokenApi.class)
                .onErrorMap(e -> new WebException(e.getMessage()))
                .block();

        webClient = webClient.mutate()
                .defaultHeaders(httpHeaders -> httpHeaders.setBearerAuth(token.getAccessToken()))
                .build();

        logger.info("Access token saved successfully...");
    }

    protected void getTokenUsingClientCredentials(String clientId, String clientSecret) {
        token = webClient.post().uri(uriBuilder()
                .path(authUri)
                .queryParam("grant_type", "client_credentials")
                .build().toString())
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .retrieve()
                .bodyToMono(TokenApi.class)
                .onErrorMap(e -> new WebException(e.getMessage()))
                .block();

        webClient = webClient.mutate()
                .defaultHeaders(httpHeaders -> httpHeaders.setBearerAuth(token.getAccessToken()))
                .build();

        logger.info("Access token saved successfully...");
    }

    protected Optional<Object> checkAccessToken() {
        return getWebClient().get().uri(uriBuilder()
                .pathSegment("oauth")
                .pathSegment("check_token")
                .queryParam("token", token.getAccessToken())
                .build().toString())
                .retrieve()
                .bodyToMono(Object.class)
                .doOnError(error -> logger.error(error.getMessage()))
                .blockOptional();
    }

    protected UriBuilder uriBuilder() {
        return new DefaultUriBuilderFactory().builder();
    }

    protected WebClient getWebClient() {
        return webClient;
    }

    protected TokenApi getToken() {
        return token;
    }

    protected <T> T get(URI uri, Class<T> responseClazz) {
        return webClient.get().uri(uri.toString())
                .retrieve()
                .bodyToMono(responseClazz)
                .onErrorMap(e -> new WebException(e.getMessage()))
                .block();
    }

    protected <T> T post(URI uri, Class<T> responseClazz) {
        return post(uri, EMPTY, responseClazz);
    }

    protected <T> T post(URI uri, Object body, Class<T> responseClazz) {
        return webClient.post().uri(uri.toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseClazz)
                .onErrorMap(e -> new WebException(e.getMessage()))
                .block();
    }

    protected <T> T put(URI uri, Class<T> responseClazz) {
        return put(uri, EMPTY, responseClazz);
    }

    protected <T> T put(URI uri, Object body, Class<T> responseClazz) {
        return webClient.put().uri(uri.toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseClazz)
                .onErrorMap(e -> new WebException(e.getMessage()))
                .block();
    }

    protected <T> T patch(URI uri, Class<T> responseClazz) {
        return patch(uri, EMPTY, responseClazz);
    }

    protected <T> T patch(URI uri, Object body, Class<T> responseClazz) {
        return webClient.patch().uri(uri.toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseClazz)
                .onErrorMap(e -> new WebException(e.getMessage()))
                .block();
    }

}

