package usociety.manager.domain.service.web;

import java.net.URI;
import java.util.List;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import usociety.manager.domain.provider.authentication.dto.TokenDTO;

public interface AbstractConnector {

    void setUp(String url, int timeOut);

    void setUp(String url, int timeOut, String authUrl);

    TokenDTO getToken(String clientId, String clientSecret, String username, String password);

    TokenDTO getToken(String clientId, String clientSecret);

    UriBuilder uriBuilder();

    WebClient getWebClient();

    <T> T get(URI uri, Class<T> responseClazz);

    <T> List<T> getList(URI uri, Class<T> responseClazz);

    <T> T post(URI uri, Class<T> responseClazz);

    <T> T post(URI uri, Object body, Class<T> responseClazz);

    <T> List<T> postList(URI uri, Object body, Class<T> responseClazz);

    <T> T put(URI uri, Class<T> responseClazz);

    <T> T put(URI uri, Object body, Class<T> responseClazz);

    <T> T patch(URI uri, Class<T> responseClazz);

    <T> T patch(URI uri, Object body, Class<T> responseClazz);

    <T> T delete(URI uri, Class<T> responseClazz);

}
