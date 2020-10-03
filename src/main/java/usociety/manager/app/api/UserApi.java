package usociety.manager.app.api;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.util.BaseObject;

@JsonIgnoreProperties( { "password" })
@ApiModel(value = "User Api")
@JsonRootName(value = "user")
public class UserApi extends BaseObject {

    @JsonProperty
    private boolean accountLocked;

    @JsonProperty
    private LocalDate birthDate;

    @JsonProperty
    private LocalDate createdAt;

    @JsonProperty
    private String documentNumber;

    @JsonProperty
    private String email;

    @JsonProperty
    private boolean emailVerified;

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private LocalDateTime lastAccessAt;

    @JsonProperty
    private String gender;

    @JsonProperty
    private String password;

    @JsonProperty
    private String phoneNumber;

    @JsonProperty
    private String username;

    public UserApi() {
        super();
    }

    private UserApi(Builder builder) {
        accountLocked = builder.accountLocked;
        birthDate = builder.birthDate;
        createdAt = builder.createdAt;
        documentNumber = builder.documentNumber;
        email = builder.email;
        emailVerified = builder.emailVerified;
        firstName = builder.firstName;
        lastName = builder.lastName;
        lastAccessAt = builder.lastAccessAt;
        gender = builder.gender;
        password = builder.password;
        phoneNumber = builder.phoneNumber;
        username = builder.username;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getLastAccessAt() {
        return lastAccessAt;
    }

    public void setLastAccessAt(LocalDateTime lastAccessAt) {
        this.lastAccessAt = lastAccessAt;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

        private boolean accountLocked;
        private LocalDate birthDate;
        private LocalDate createdAt;
        private String documentNumber;
        private String email;
        private boolean emailVerified;
        private String firstName;
        private String lastName;
        private LocalDateTime lastAccessAt;
        private String gender;
        private String password;
        private String phoneNumber;
        private String username;

        private Builder() {
            super();
        }

        public Builder accountLocked(boolean accountLocked) {
            this.accountLocked = accountLocked;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder createdAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder emailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder lastAccessAt(LocalDateTime lastAccessAt) {
            this.lastAccessAt = lastAccessAt;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public UserApi build() {
            return new UserApi(this);
        }

    }

}
