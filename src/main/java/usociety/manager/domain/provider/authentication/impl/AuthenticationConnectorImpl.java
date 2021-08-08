package usociety.manager.domain.provider.authentication.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import usociety.manager.app.api.TokenApi;
import usociety.manager.app.rest.request.LoginRequest;
import usociety.manager.domain.converter.Converter;
import usociety.manager.domain.provider.authentication.AuthenticationConnector;
import usociety.manager.domain.provider.authentication.dto.TokenDTO;
import usociety.manager.domain.provider.web.impl.ReactiveConnectorImpl;

@Component
public class AuthenticationConnectorImpl extends ReactiveConnectorImpl implements AuthenticationConnector {

    private final String clientSecret;
    private final String clientId;

    @Autowired
    public AuthenticationConnectorImpl(@Value("${web.authentication.client-secret}") String clientSecret,
                                       @Value("${web.connection.time-out:5000}") int connectionTimeOut,
                                       @Value("${web.authentication.client-id}") String clientId,
                                       @Value("${web.read.time-out:30000}") int readTimeOut,
                                       @Value("${web.authentication.path}") String authPath,
                                       @Value("${web.authentication.url}") String baseUrl) {
        super(baseUrl, authPath, readTimeOut, connectionTimeOut);
        this.clientSecret = clientSecret;
        this.clientId = clientId;
    }

    @Override
    public TokenApi login(LoginRequest body) {
        TokenDTO token = getToken(clientId, clientSecret, body.getUsername(), body.getPassword());
        return Converter.token(token);
    }

}
