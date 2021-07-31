package usociety.manager.domain.provider.authentication.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import usociety.manager.app.api.TokenApi;
import usociety.manager.app.rest.request.LoginRequest;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.provider.authentication.AuthenticationConnector;
import usociety.manager.domain.provider.authentication.dto.TokenDTO;
import usociety.manager.domain.service.web.impl.AbstractConnectorImpl;

@Component
public class AuthenticationConnectorImpl extends AbstractConnectorImpl implements AuthenticationConnector {

    @Value("${web.authentication.url}")
    private String baseUrl;
    @Value("${web.authentication.path}")
    private String authPath;
    @Value("${web.authentication.client-id}")
    private String clientId;
    @Value("${web.authentication.client-secret}")
    private String clientSecret;
    @Value("${web.read-time-out:5}")
    private int timeOut;

    @PostConstruct
    private void init() {
        setUp(baseUrl, timeOut, authPath);
    }

    @Override
    public TokenApi login(LoginRequest body) {
        TokenDTO token = getToken(clientId, clientSecret, body.getUsername(), body.getPassword());
        return Converter.token(token);
    }

}
