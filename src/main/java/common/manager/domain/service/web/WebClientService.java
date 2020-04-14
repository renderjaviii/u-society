package common.manager.domain.service.web;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import common.manager.app.api.TokenApi;
import common.manager.domain.exception.WebException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.internal.StringUtil;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Service
public class WebClientService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private WebClient webClient;
    private TokenApi token;

    protected void setUp(String url, int timeOut) {
        LOGGER.info("Creating WebClient for host: {}", url);

        if (webClient == null) {
            if (!StringUtil.isNullOrEmpty(url)) {
                buildWebClient(url);
            } else {
                throwUrlException();
            }
            if (timeOut != 0) {
                setTcpClient(timeOut);
            }
        }
    }

    private void buildWebClient(String url) {
        webClient = WebClient.builder()
                .baseUrl(url)
                .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build();
    }

    private void setTcpClient(int timeOut) {
        // TODO -> comprobar funcionalidad
        TcpClient tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOut)
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(timeOut, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(timeOut, TimeUnit.MILLISECONDS));
                });

        webClient = webClient.mutate()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .build();
    }

    protected void getTokenUsingPassword(String clientId,
                                         String clientSecret,
                                         String username,
                                         String password) {
        token = webClient.post()
                .uri(uriBuilder -> uriBuilder()
                        .path("oauth/token")
                        .queryParam("grant_type", "password")
                        .build())
                .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .bodyValue(BodyInserters.fromFormData("username", username).with("password", password))
                .retrieve()
                .bodyToMono(TokenApi.class)
                .onErrorMap(e -> new WebException(e.getMessage()))
                .block();

        webClient = webClient.mutate()
                .defaultHeaders(httpHeaders -> httpHeaders.setBearerAuth(token.getAccessToken()))
                .build();

        LOGGER.info("Access token saved successfully...");
    }

    protected void getTokenUsingClientCredentials(String clientId, String clientSecret) {
        token = webClient.post()
                .uri(uriBuilder -> uriBuilder()
                        .path("oauth/token")
                        .queryParam("grant_type", "client_credentials")
                        .build())
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .retrieve()
                .bodyToMono(TokenApi.class)
                .onErrorMap(e -> new WebException(e.getMessage()))
                .block();

        webClient = webClient.mutate()
                .defaultHeaders(httpHeaders -> httpHeaders.setBearerAuth(token.getAccessToken()))
                .build();

        LOGGER.info("Access token saved successfully...");
    }

    protected Optional<Object> checkAccessToken() {
        return getWebClient().get()
                .uri(uriBuilder -> uriBuilder()
                        .path("oauth/check_token")
                        .queryParam("token", token.getAccessToken())
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .doOnError(error -> LOGGER.error(error.getMessage()))
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
        return webClient.get()
                .uri(uri.toString())
                .retrieve()
                .bodyToMono(responseClazz)
                .onErrorMap(e -> new WebException(e.getMessage()))
                .block();
    }

    protected <T> T post(URI uri, Class<T> responseClazz) {
        return post(uri, null, responseClazz);
    }

    protected <T> T post(URI uri, Object body, Class<T> responseClazz) {
        return webClient.post()
                .uri(uri.toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseClazz)
                .onErrorMap(e -> new WebException(e.getMessage()))
                .block();
    }

    protected <T> T put(URI uri, Class<T> responseClazz) {
        return put(uri, null, responseClazz);
    }

    protected <T> T put(URI uri, Object body, Class<T> responseClazz) {
        return webClient.put()
                .uri(uri.toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseClazz)
                .onErrorMap(e -> new WebException(e.getMessage()))
                .block();
    }

    protected <T> T patch(URI uri, Class<T> responseClazz) {
        return patch(uri, null, responseClazz);
    }

    protected <T> T patch(URI uri, Object body, Class<T> responseClazz) {
        return webClient.patch()
                .uri(uri.toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseClazz)
                .onErrorMap(e -> new WebException(e.getMessage()))
                .block();
    }

    private void throwUrlException() {
        throw new UnsupportedOperationException("The base url is invalid.");
    }

}

