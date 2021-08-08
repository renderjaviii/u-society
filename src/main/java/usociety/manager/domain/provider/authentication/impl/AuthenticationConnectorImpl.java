package usociety.manager.domain.provider.authentication.impl;

import static usociety.manager.domain.provider.web.RestClientFactoryBuilder.newBuilder;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import usociety.manager.app.api.TokenApi;
import usociety.manager.app.rest.request.LoginRequest;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.provider.authentication.AuthenticationConnector;
import usociety.manager.domain.provider.authentication.dto.TokenDTO;
import usociety.manager.domain.provider.web.RestClient;
import usociety.manager.domain.provider.web.RestClientFactory;

@Component
public class AuthenticationConnectorImpl implements AuthenticationConnector {

    private final RestClient restClient;

    @Autowired
    public AuthenticationConnectorImpl(@Value("${server.ssl.key-store-password}") String keyStorePassword,
                                       @Value("${web.authentication.client-secret}") String clientSecret,
                                       @Value("${server.ssl.key-store-type}") String keyStoreType,
                                       @Value("${web.authentication.client-id}") String clientId,
                                       @Value("${web.authentication.path}") String authPath,
                                       @Value("${server.ssl.key-store}") Resource keyStore,
                                       @Value("${web.authentication.url}") String baseURL,
                                       RestClientFactory restClientFactory) {
        this.restClient = restClientFactory.create(newBuilder()
                .keyStorePassword(keyStorePassword)
                .keyStoreType(keyStoreType)
                .clientSecret(clientSecret)
                .keyStore(keyStore)
                .authPath(authPath)
                .clientId(clientId)
                .baseURL(baseURL)
                .build());
    }

    @PostConstruct
    private void init() {
        restClient.setUp();
    }

    @Override
    public TokenApi login(LoginRequest body) {
        TokenDTO token = restClient.getToken(body.getUsername(), body.getPassword());
        return Converter.token(token);
    }

}
