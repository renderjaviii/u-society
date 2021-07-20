package usociety.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import usociety.manager.app.util.BaseObject;

@Entity
@Table(name = "otps")
public class Otp extends BaseObject {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "otp_code", nullable = false)
    private String otpCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "username_owner", nullable = false, updatable = false)
    private String usernameOwner;

    @Column(name = "email_owner", nullable = false)
    private String emailOwner;

    public Otp() {
        super();
    }

    private Otp(Builder builder) {
        id = builder.id;
        otpCode = builder.otpCode;
        createdAt = builder.createdAt;
        expiresAt = builder.expiresAt;
        active = builder.active;
        usernameOwner = builder.ownerUsername;
        emailOwner = builder.emailOwner;
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

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getUsernameOwner() {
        return usernameOwner;
    }

    public String getEmailOwner() {
        return emailOwner;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Otp)) {
            return false;
        }
        return Objects.equals(((Otp) obj).id, id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static final class Builder {

        private Long id;
        private String otpCode;
        private LocalDateTime createdAt;
        private LocalDateTime expiresAt;
        private Boolean active;
        private String ownerUsername;
        private String emailOwner;

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

        public Builder active(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder ownerUsername(String ownerUsername) {
            this.ownerUsername = ownerUsername;
            return this;
        }

        public Builder emailOwner(String emailOwner) {
            this.emailOwner = emailOwner;
            return this;
        }

        public Otp build() {
            return new Otp(this);
        }

    }

}
