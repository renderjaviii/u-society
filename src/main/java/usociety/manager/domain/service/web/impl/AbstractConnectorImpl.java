package usociety.manager.domain.service.web.impl;

import static java.lang.Boolean.TRUE;
import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static usociety.manager.domain.exception.UserValidationException.LOGIN_ERROR;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

import javax.net.ssl.SSLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.internal.StringUtil;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import usociety.manager.app.api.ApiError;
import usociety.manager.domain.exception.WebException;
import usociety.manager.domain.provider.authentication.dto.TokenDTO;
import usociety.manager.domain.service.web.AbstractConnector;

@Component
public class AbstractConnectorImpl implements AbstractConnector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private WebClient webClient;
    private String baseUrl;
    private String authPath;

    @Override
    public void setUp(String url, int timeOut) {
        logger.info("Creating WebClient for host: {}", url);
        if (StringUtil.isNullOrEmpty(url)) {
            throw new UnsupportedOperationException("The base url is invalid.");
        }
        init(url, timeOut, EMPTY);
    }

    @Override
    public void setUp(String url, int timeOut, String authUrl) {
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
        authPath = authUrl;
        buildWebClient(timeOut);
    }

    private void buildWebClient(int timeOut) {

        try {
            HttpClient httpClient = HttpClient.create()
                    .tcpConfiguration(client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                            .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(timeOut))));

            SslContext sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();

            httpClient.secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));

            webClient = WebClient.builder()
                    .baseUrl(baseUrl)
                    .clientConnector(new ReactorClientHttpConnector(httpClient.wiretap(TRUE)))
                    .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                    .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .filter(responseFilter())
                    .build();
        } catch (SSLException ignore) {
        }
    }

    private ExchangeFilterFunction responseFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            if (response.statusCode().isError()) {
                return response.bodyToMono(ApiError.class)
                        .flatMap(error -> Mono.error(new WebException(error)));
            }
            return Mono.just(response);
        });
    }

    @Override
    public TokenDTO getToken(String clientId, String clientSecret, String username, String password) {
        logger.info("Trying to get token: {}{}", baseUrl, authPath);

        TokenDTO token = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .build()
                .post().uri(uriBuilder()
                        .path(authPath)
                        .queryParam("grant_type", "password")
                        .build().toString())
                .body(BodyInserters.fromFormData("username", username).with("password", password))
                .headers(httpHeaders -> httpHeaders.setBasicAuth(clientId, clientSecret))
                .retrieve()
                .onStatus(HttpStatus::isError, buildTokenError())
                .bodyToMono(TokenDTO.class)
                .block();

        logger.info("Access token obtained successfully.");
        return token;
    }

    @Override
    public TokenDTO getToken(String clientId, String clientSecret) {
        TokenDTO token = webClient.post().uri(uriBuilder()
                .path(authPath)
                .queryParam("grant_type", "client_credentials")
                .build().toString())
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .retrieve()
                .onStatus(HttpStatus::isError, buildTokenError())
                .bodyToMono(TokenDTO.class)
                .block();

        logger.info("Access token obtained successfully: {}", token);
        return token;
    }

    @Override
    public UriBuilder uriBuilder() {
        return new DefaultUriBuilderFactory().builder();
    }

    @Override
    public WebClient getWebClient() {
        return webClient;
    }

    @Override
    public <T> T get(URI uri, Class<T> responseClazz) {
        return webClient.get().uri(uri.toString())
                .retrieve()
                .bodyToMono(responseClazz)
                .block();
    }

    @Override
    public <T> List<T> getList(URI uri, Class<T> responseClazz) {
        return webClient.get().uri(uri.toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<T>>() {
                })
                .block();
    }

    @Override
    public <T> T post(URI uri, Class<T> responseClazz) {
        return post(uri, EMPTY, responseClazz);
    }

    @Override
    public <T> T post(URI uri, Object body, Class<T> responseClazz) {
        return webClient.post().uri(uri.toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseClazz)
                .block();
    }

    @Override
    public <T> List<T> postList(URI uri, Object body, Class<T> responseClazz) {
        return webClient.post().uri(uri.toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<T>>() {
                })
                .block();
    }

    @Override
    public <T> T put(URI uri, Class<T> responseClazz) {
        return put(uri, EMPTY, responseClazz);
    }

    @Override
    public <T> T put(URI uri, Object body, Class<T> responseClazz) {
        return webClient.put().uri(uri.toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseClazz)
                .block();
    }

    @Override
    public <T> T patch(URI uri, Class<T> responseClazz) {
        return patch(uri, EMPTY, responseClazz);
    }

    @Override
    public <T> T patch(URI uri, Object body, Class<T> responseClazz) {
        return webClient.patch().uri(uri.toString())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseClazz)
                .block();
    }

    @Override
    public <T> T delete(URI uri, Class<T> responseClazz) {
        return webClient.delete().uri(uri.toString())
                .retrieve()
                .bodyToMono(responseClazz)
                .block();
    }

    private Function<ClientResponse, Mono<? extends Throwable>> buildTokenError() {
        return response -> response.bodyToMono(ApiError.class)
                .flatMap(error -> Mono.error(new WebException(error.getErrorDescription(), LOGIN_ERROR)));
    }

    /*webClient = webClient.mutate()
                .defaultHeaders(httpHeaders -> httpHeaders.setBearerAuth(token.getAccessToken()))
                .build();*/
}

