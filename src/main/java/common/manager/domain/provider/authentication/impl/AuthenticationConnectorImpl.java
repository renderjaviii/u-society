package common.manager.domain.provider.authentication.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import common.manager.app.rest.request.UserLoginRequest;
import common.manager.domain.provider.authentication.AuthenticationConnector;
import common.manager.domain.provider.authentication.dto.TokenDTO;
import common.manager.domain.service.web.WebClientService;

@Component
public class AuthenticationConnectorImpl extends WebClientService implements AuthenticationConnector {

    @Value("${provider.authentication-service.url}")
    private String baseUrl;
    @Value("${provider.authentication-service.authentication-path}")
    private String authPath;
    @Value("${provider.authentication-client.id}")
    private String clientId;
    @Value("${provider.authentication-client.secret}")
    private String clientSecret;
    @Value("${provider.web-service.time-out:5}")
    private int timeOut;

    @PostConstruct
    private void init() {
        setUp(baseUrl, timeOut, authPath);
    }

    @Override
    public TokenDTO login(UserLoginRequest request) {
        return getToken(clientId, clientSecret, request.getUsername(), request.getPassword());
    }

}
