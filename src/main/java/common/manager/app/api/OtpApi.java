package common.manager.app.api;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "OTP Api")
public class OtpApi {

    @JsonProperty(value = "id")
    private Long id;
    @JsonProperty(value = "otpCode")
    private String otpCode;
    @JsonProperty(value = "createdAt")
    private LocalDateTime createdAt;
    @JsonProperty(value = "expiresAt")
    private LocalDateTime expiresAt;
    @JsonProperty(value = "active")
    private boolean active;
    @JsonProperty(value = "ownerUsername")
    private String ownerUsername;

    public OtpApi() {
        super();
    }

    private OtpApi(Builder builder) {
        id = builder.id;
        otpCode = builder.otpCode;
        createdAt = builder.createdAt;
        expiresAt = builder.expiresAt;
        active = builder.active;
        ownerUsername = builder.ownerUsername;
    }

    public static Builder newBuilder() {
        return new Builder();
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

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public static final class Builder {

        private Long id;
        private String otpCode;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private boolean active;
        private String ownerUsername;

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

        public Builder ownerUsername(String ownerUsername) {
            this.ownerUsername = ownerUsername;
            return this;
        }

        public OtpApi build() {
            return new OtpApi(this);
        }

    }

}
