package usociety.manager.app.api;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import usociety.manager.app.util.BaseObject;

public class OtpApi extends BaseObject {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String otpCode;

    @JsonProperty
    private LocalDateTime createdAt;

    @JsonProperty
    private LocalDateTime expiresAt;

    @JsonProperty
    private boolean active;

    @JsonProperty
    private String usernameOwner;

    @JsonProperty
    private String userEmailOwner;

    public OtpApi() {
        super();
    }

    private OtpApi(Builder builder) {
        id = builder.id;
        otpCode = builder.otpCode;
        createdAt = builder.createdAt;
        expiresAt = builder.expiresAt;
        active = builder.active;
        usernameOwner = builder.usernameOwner;

        userEmailOwner = builder.userEmailOwner;
    }

    public Long getId() {
        return id;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isActive() {
        return active;
    }

    public String getUserEmailOwner() {
        return userEmailOwner;
    }

    public String getUsernameOwner() {
        return usernameOwner;
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

        private Long id;
        private String otpCode;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private boolean active;
        private String usernameOwner;
        private String userEmailOwner;

        private Builder() {
            super();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder otpCode(String otpCode) {
            this.otpCode = otpCode;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder expiresAt(LocalDateTime expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder usernameOwner(String usernameOwner) {
            this.usernameOwner = usernameOwner;
            return this;
        }

        public Builder userEmailOwner(String userEmailOwner) {
            this.userEmailOwner = userEmailOwner;
            return this;
        }

        public OtpApi build() {
            return new OtpApi(this);
        }

    }

}
