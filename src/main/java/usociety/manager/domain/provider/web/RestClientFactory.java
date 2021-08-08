package usociety.manager.domain.provider.web;

public interface RestClientFactory {

    RestClient create(RestClientFactoryBuilder builder);

}
