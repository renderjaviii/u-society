package common.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "credential")
public class Credential {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "client_id", unique = true, nullable = false)
    private String clientId;

    @Column(name = "client_secret", nullable = false)
    private String clientSecret;

    @Column(name = "scope", nullable = false)
    private String scopes;

    @Column(name = "grant_type", nullable = false)
    private String grantTypes;

    @Column(name = "credentials_expired", nullable = false, insertable = false,
            columnDefinition = "boolean default false")
    private Boolean credentialsExpired;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "description")
    private String desctiption;

    public Credential() {
        super();
    }

    public Credential(Credential credential) {
        this.clientId = credential.clientId;
        this.clientSecret = credential.clientSecret;
        this.grantTypes = credential.grantTypes;
        this.scopes = credential.scopes;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getScopes() {
        return scopes;
    }

    public String getGrantTypes() {
        return grantTypes;
    }

    public Boolean getCredentialsExpired() {
        return credentialsExpired;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public String getDesctiption() {
        return desctiption;
    }

}
