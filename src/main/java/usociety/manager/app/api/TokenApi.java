package usociety.manager.app.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import usociety.manager.app.util.BaseObject;

public class TokenApi extends BaseObject {

    @JsonProperty
    private String accessToken;

    @JsonProperty
    private String tokenType;

    @JsonProperty
    private String refreshToken;

    @JsonProperty
    private String expiresIn;

    @JsonProperty
    private String scope;

    @JsonProperty
    private String jti;

    public TokenApi() {
        super();
    }

    private TokenApi(Builder builder) {
        accessToken = builder.accessToken;
        tokenType = builder.tokenType;
        refreshToken = builder.refreshToken;
        expiresIn = builder.expiresIn;
        scope = builder.scope;
        jti = builder.jti;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public String getJti() {
        return jti;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private String accessToken;
        private String tokenType;
        private String refreshToken;
        private String expiresIn;
        private String scope;
        private String jti;

        private Builder() {
            super();
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder refreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder expiresIn(String expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder jti(String jti) {
            this.jti = jti;
            return this;
        }

        public TokenApi build() {
            return new TokenApi(this);
        }

    }

}
