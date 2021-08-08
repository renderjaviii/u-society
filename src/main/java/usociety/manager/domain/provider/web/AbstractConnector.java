package usociety.manager.domain.provider.web;

import java.net.URI;
import java.util.function.Function;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import usociety.manager.domain.provider.authentication.dto.TokenDTO;

public interface AbstractConnector {

    TokenDTO getToken(String clientId, String clientSecret, String username, String password);

    TokenDTO getToken(String clientId, String clientSecret);

    <T> T get(Function<UriBuilder, URI> uriFunction, Class<T> responseClazz);

    <T> T get(Function<UriBuilder, URI> uriFunction, ParameterizedTypeReference<T> typeReference);

    <T> T post(Function<UriBuilder, URI> uriFunction, Class<T> responseClazz);

    <T> T post(Function<UriBuilder, URI> uriFunction, Object body, Class<T> responseClazz);

    <T> T post(Function<UriBuilder, URI> uriFunction, Object body, ParameterizedTypeReference<T> typeReference);

    <T> T put(Function<UriBuilder, URI> uriFunction, Class<T> responseClazz);

    <T> T put(Function<UriBuilder, URI> uriFunction, Object body, Class<T> responseClazz);

    <T> T patch(Function<UriBuilder, URI> uriFunction, Class<T> responseClazz);

    <T> T patch(Function<UriBuilder, URI> uriFunction, Object body, Class<T> responseClazz);

    <T> T delete(Function<UriBuilder, URI> uriFunction, Class<T> responseClazz);

    WebClient buildWebClient();

}
