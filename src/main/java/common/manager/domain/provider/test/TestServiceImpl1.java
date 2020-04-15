package common.manager.domain.provider.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import common.manager.domain.service.web.WebClientService;

@Service
@Qualifier("service1")
public class TestServiceImpl1 extends WebClientService implements TestService {

    @Value("${web-service.url:http://localhost:8079}")
    private String baseUrl;
    @Value("${web-service.auth-url:/oauth/token}")
    private String authUrl;
    @Value("${web-service.time-out:5}")
    private int timeOut;

    @PostConstruct
    private void init() {
        setUp(baseUrl, timeOut, authUrl);
    }

    @Override
    public void example() {
        getTokenUsingPassword("clientId", "secret", "test", "test");
        Optional<Object> optional = checkAccessToken();

        MultiValueMap<String, String> qParams = new LinkedMultiValueMap<>();
        qParams.add("description", "1234");
        Map<String, String> pParams = new HashMap<>();
        pParams.put("id", "1");

        Test test = get(uriBuilder()
                        .path("/test/{id}")
                        .queryParams(qParams)
                        .build(pParams),
                Test.class);

        System.out.println(test);

    }

}
