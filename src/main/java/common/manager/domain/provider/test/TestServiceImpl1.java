package common.manager.domain.provider.test;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    public void get() {

        getTokenUsingClientCredentials("clientId", "secret");
        Optional<Object> optional = checkAccessToken();

        getWebClient().get()
                .uri(uriBuilder -> getLocalUriBuilder()
                        .path("/test/{id}")
                        .queryParam("description", "Perrazo")
                        .build(101010))
                .retrieve()
                .bodyToMono(Test.class)
                .block();

    }

}
