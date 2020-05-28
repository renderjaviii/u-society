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

    @Value("${web-service.url:http://localhost:8075/authentication}")
    private String baseUrl;
    @Value("${web-service.auth-url:/oauth/token}")
    private String authUrl;
    @Value("${web-service.time-out:5}")
    private int timeOut;
    @Value("${authentication-client.id}")
    private String clientId;
    @Value("${authentication-client.secret}")
    private String clientSecret;

    @PostConstruct
    private void init() {
        setUp(baseUrl, timeOut, authUrl);
    }

    @Override
    public TokenDTO login(UserLoginRequest request) {
        return getTokenUsingPassword(clientId, clientSecret, request.getUsername(), request.getPassword());
    }

    /*
        MultiValueMap<String, String> qParams = new LinkedMultiValueMap<>();
        qParams.add("description", "1234");
        Map<String, String> pParams = new HashMap<>();
        pParams.put("id", "1");
        Test test = get(uriBuilder()
                        .path("/test/{id}")
                        .queryParams(qParams)
                        .build(pParams),
                Test.class);
    */

}
