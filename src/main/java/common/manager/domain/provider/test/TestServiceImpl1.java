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
    private String url;
    @Value("${web-service.time-out:5000}")
    private int timeOut;

    @PostConstruct
    private void init() {
        setUp(url, timeOut);
    }

    @Override
    public void example() {

        getTokenUsingClientCredentials("clientId", "secret");
        Optional<Object> optional = checkAccessToken();

        MultiValueMap<String, String> qParams = new LinkedMultiValueMap<>();
        qParams.add("description", "1234");
        Map<String, String> pParams = new HashMap<>();
        pParams.put("id", "1");

        Test test = post(uriBuilder()
                        .pathSegment("/test")
                        .pathSegment("{id}")
                        .queryParams(qParams)
                        .build(pParams),
                Test.class);

        System.out.println(test);

    }

}
