package common.manager.domain.provider.user.dto;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "User DTO")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "user")
public class UserDTO {

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    @JsonProperty
    private String firstName;

    @JsonProperty
    private String lastName;

    @JsonProperty
    private String documentNumber;

    @JsonProperty
    private String email;

    @JsonProperty
    private String gender;

    @JsonProperty
    private LocalDate birthDate;

    @JsonProperty
    private String phoneNumber;

    @JsonProperty
    private LocalDate createdAt;

    @JsonProperty
    private LocalDateTime lastAccessAt;

    @JsonProperty
    private boolean accountLocked;

    @JsonProperty
    private boolean emailVerified;

    public UserDTO() {
        super();
    }

    private UserDTO(Builder builder) {
        username = builder.username;
        password = builder.password;
        firstName = builder.firstName;
        lastName = builder.lastName;
        documentNumber = builder.documentNumber;
        email = builder.email;
        gender = builder.gender;
        birthDate = builder.birthDate;
        phoneNumber = builder.phoneNumber;
        createdAt = builder.createdAt;
        lastAccessAt = builder.lastAccessAt;
        accountLocked = builder.accountLocked;
        emailVerified = builder.emailVerified;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastAccessAt() {
        return lastAccessAt;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        return reflectionEquals(this, o);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String documentNumber;
        private String email;
        private String gender;
        private LocalDate birthDate;
        private String phoneNumber;
        private LocalDate createdAt;
        private LocalDateTime lastAccessAt;
        private boolean accountLocked;
        private boolean emailVerified;

        private Builder() {
            super();
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
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

        public Builder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder createdAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder lastAccessAt(LocalDateTime lastAccessAt) {
            this.lastAccessAt = lastAccessAt;
            return this;
        }

        public Builder accountLocked(boolean accountLocked) {
            this.accountLocked = accountLocked;
            return this;
        }

        public Builder emailVerified(boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(this);
        }

    }

}

