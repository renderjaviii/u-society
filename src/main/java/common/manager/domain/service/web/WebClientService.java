package common.manager.domain.service.web;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
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

    private TokenApi token;

    private WebClient webClient;
    private URI uri;

    private void setTcpClient(int timeOut) {
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

    protected void setUp(String url, int timeOut) {
        LOGGER.info("Creating WebClient for host: {}", url);

        if (webClient == null) {
            if (!StringUtil.isNullOrEmpty(url)) {
                try {
                    uri = new URI(url);
                    webClient = WebClient.builder()
                            .defaultHeader(ACCEPT, APPLICATION_JSON_VALUE)
                            .defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                            .build();

                } catch (URISyntaxException e) {
                    throwUrlException();
                }
            } else {
                throwUrlException();
            }

            if (timeOut != 0) {
                setTcpClient(timeOut);
            }
        }
    }

    protected void getTokenUsingClientCredentials(String clientId, String clientSecret) {
        token = webClient.post()
                .uri(uriBuilder -> getLocalUriBuilder()
                        .path("oauth/token")
                        .queryParam("grant_type", "client_credentials")
                        .build())
                .headers(headers -> headers.setBasicAuth(clientId, clientSecret))
                .retrieve()
                .bodyToMono(TokenApi.class)
                .onErrorMap(e -> new WebException(e.getMessage()))
                .block();

        webClient = webClient.mutate()
                .defaultHeaders(httpHeaders ->
                        httpHeaders.setBearerAuth(token.getAccessToken()))
                .build();

        LOGGER.info("Access token saved successfully...");
    }

    protected Optional<Object> checkAccessToken() {
        return getWebClient().get()
                .uri(uriBuilder -> getLocalUriBuilder()
                        .path("oauth/check_token")
                        .queryParam("token", token.getAccessToken())
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .doOnError(error -> LOGGER.error(error.getMessage()))
                .blockOptional();
    }

    public UriBuilder getLocalUriBuilder() {
        return new DefaultUriBuilderFactory().builder()
                .scheme(uri.getScheme())
                .host(uri.getHost())
                .port(uri.getPort());
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public TokenApi getToken() {
        return token;
    }

    /*  private  <T> T get(UriBuilder uriBuilder){
              webClient.get()
                      .uri(uriBuilder::build)
                      .retrieve();
          }
      */
    private void throwUrlException() {
        throw new UnsupportedOperationException("The base url is invalid.");
    }

}

