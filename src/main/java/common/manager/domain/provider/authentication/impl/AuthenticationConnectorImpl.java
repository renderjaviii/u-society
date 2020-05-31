package common.manager.domain.provider.authentication.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import common.manager.app.api.TokenApi;
import common.manager.app.rest.request.UserLoginRequest;
import common.manager.domain.converter.Converter;
import common.manager.domain.provider.authentication.AuthenticationConnector;
import common.manager.domain.provider.authentication.dto.TokenDTO;
import common.manager.domain.service.web.impl.AbstractConnectorImpl;

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
    @Value("${web.time-out}")
    private int timeOut;

    @PostConstruct
    private void init() {
        setUp(baseUrl, timeOut, authPath);
    }

    @Override
    public TokenApi login(UserLoginRequest body) {
        TokenDTO token = getToken(clientId, clientSecret, body.getUsername(), body.getPassword());
        return Converter.token(token);
    }

}
