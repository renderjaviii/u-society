package usociety.manager.domain.provider.web;

import java.time.Clock;

import org.springframework.core.io.Resource;

import usociety.manager.app.util.BaseObject;

public class RestClientFactoryBuilder extends BaseObject {

    private Integer connectionTimeOut;
    private String keyStorePassword;
    private String keyStoreType;
    private String clientSecret;
    private Integer readTimeOut;
    private Resource keyStore;
    private String clientId;
    private String authPath;
    private String baseURL;
    private Clock clock;

    public RestClientFactoryBuilder() {
        super();
    }

    private RestClientFactoryBuilder(Builder builder) {
        connectionTimeOut = builder.connectionTimeOut;
        keyStorePassword = builder.keyStorePassword;
        keyStoreType = builder.keyStoreType;
        clientSecret = builder.clientSecret;
        readTimeOut = builder.readTimeOut;
        keyStore = builder.keyStore;
        clientId = builder.clientId;
        authPath = builder.authPath;
        baseURL = builder.baseURL;
        clock = builder.clock;
    }

    public static Builder newBuilder(RestClientFactoryBuilder copy) {
        Builder builder = new Builder();
        builder.connectionTimeOut = copy.getConnectionTimeOut();
        builder.keyStorePassword = copy.getKeyStorePassword();
        builder.keyStoreType = copy.getKeyStoreType();
        builder.clientSecret = copy.getClientSecret();
        builder.readTimeOut = copy.getReadTimeOut();
        builder.keyStore = copy.getKeyStore();
        builder.clientId = copy.getClientId();
        builder.authPath = copy.getAuthPath();
        builder.baseURL = copy.getBaseURL();
        builder.clock = copy.getClock();
        return builder;
    }

    public Integer getConnectionTimeOut() {
        return connectionTimeOut;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public Integer getReadTimeOut() {
        return readTimeOut;
    }

    public Resource getKeyStore() {
        return keyStore;
    }

    public String getClientId() {
        return clientId;
    }

    public String getAuthPath() {
        return authPath;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public Clock getClock() {
        return clock;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private Integer connectionTimeOut;
        private String keyStorePassword;
        private String keyStoreType;
        private String clientSecret;
        private Integer readTimeOut;
        private Resource keyStore;
        private String clientId;
        private String authPath;
        private String baseURL;
        private Clock clock;

        private Builder() {
            super();
        }

        public Builder connectionTimeOut(Integer connectionTimeOut) {
            this.connectionTimeOut = connectionTimeOut;
            return this;
        }

        public Builder keyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
            return this;
        }

        public Builder keyStoreType(String keyStoreType) {
            this.keyStoreType = keyStoreType;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder readTimeOut(Integer readTimeOut) {
            this.readTimeOut = readTimeOut;
            return this;
        }

        public Builder keyStore(Resource keyStore) {
            this.keyStore = keyStore;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder authPath(String authPath) {
            this.authPath = authPath;
            return this;
        }

        public Builder baseURL(String baseURL) {
            this.baseURL = baseURL;
            return this;
        }

        public Builder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public RestClientFactoryBuilder build() {
            return new RestClientFactoryBuilder(this);
        }

    }

}
