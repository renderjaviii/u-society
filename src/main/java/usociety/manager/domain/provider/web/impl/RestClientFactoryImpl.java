package usociety.manager.domain.provider.web.impl;

import java.time.Clock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import usociety.manager.domain.provider.web.RestClient;
import usociety.manager.domain.provider.web.RestClientFactory;
import usociety.manager.domain.provider.web.RestClientFactoryBuilder;

@Component
public class RestClientFactoryImpl implements RestClientFactory {

    private final Clock clock;

    @Autowired
    public RestClientFactoryImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public RestClient create(RestClientFactoryBuilder builder) {
        return new ReactiveRestClientImpl(RestClientFactoryBuilder
                .newBuilder(builder)
                .clock(clock)
                .build());
    }

}
