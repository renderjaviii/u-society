package common.manager.app.api;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "User Api.")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "user")
public class UserApi {

    @ApiModelProperty(value = "username")
    private String username;

    @ApiModelProperty(value = "firstName")
    private String firstName;

    @ApiModelProperty(value = "lastName")
    private String lastName;

    @ApiModelProperty(value = "birthDate")
    private LocalDate birthDate;

    @ApiModelProperty(value = "gender")
    private String gender;

    @ApiModelProperty(value = "phoneNumber")
    private String phoneNumber;

    @ApiModelProperty(value = "documentNumber")
    private String documentNumber;

    @ApiModelProperty(value = "lastAccessAt")
    private LocalDateTime lastAccessAt;

    public UserApi() {
        super();
    }

    private UserApi(Builder builder) {
        username = builder.username;
        firstName = builder.firstName;
        lastName = builder.lastName;
        birthDate = builder.birthDate;
        gender = builder.gender;
        phoneNumber = builder.phoneNumber;
        lastAccessAt = builder.lastAccessAt;
        documentNumber = builder.documentNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getLastAccessAt() {
        return lastAccessAt;
    }

    public String getDocumentNumber() {
        return documentNumber;
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
        private String firstName;
        private String lastName;
        private LocalDate birthDate;
        private String gender;
        private String phoneNumber;
        private String documentNumber;
        private LocalDateTime lastAccessAt;

        private Builder() {
            super();
        }

        public Builder username(String username) {
            this.username = username;
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

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        public Builder lastAccessAt(LocalDateTime lastAccessAt) {
            this.lastAccessAt = lastAccessAt;
            return this;
        }

        public UserApi build() {
            return new UserApi(this);
        }

    }

}
