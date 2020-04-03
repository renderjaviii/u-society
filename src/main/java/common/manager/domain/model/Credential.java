package common.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "credential")
public class Credential {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "client_id", length = 10, unique = true)
    private String clientId;

    @Column(name = "client_secret", length = 250)
    private String clientSecret;

    @Column(name = "scope", length = 250, nullable = false)
    private String scope;

    @Column(name = "grant_type", length = 250, nullable = false)
    private String grantType;

    @Column(name = "credentials_expired", nullable = false, insertable = false,
            columnDefinition = "boolean default false")
    private Boolean credentialsExpired;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @OneToOne(mappedBy = "credential", orphanRemoval = true)
    private User user;

    public Credential() {
        super();
    }

    private Credential(Builder builder) {
        id = builder.id;
        username = builder.username;
        password = builder.password;
        clientId = builder.clientId;
        clientSecret = builder.clientSecret;
        scope = builder.scope;
        grantType = builder.grantType;
        credentialsExpired = builder.credentialsExpired;
        createdAt = builder.createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getScope() {
        return scope;
    }

    public String getGrantType() {
        return grantType;
    }

    public Boolean getCredentialsExpired() {
        return credentialsExpired;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;
        private String username;
        private String password;
        private String clientId;
        private String clientSecret;
        private String scope;
        private String grantType;
        private Boolean credentialsExpired;
        private LocalDate createdAt;

        private Builder() {
            super();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public Builder grantType(String grantType) {
            this.grantType = grantType;
            return this;
        }

        public Builder credentialsExpired(Boolean credentialsExpired) {
            this.credentialsExpired = credentialsExpired;
            return this;
        }

        public Builder createdAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Credential build() {
            return new Credential(this);
        }

    }

}
