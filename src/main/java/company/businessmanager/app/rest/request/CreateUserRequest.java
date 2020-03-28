package company.businessmanager.app.rest.request;

import java.time.LocalDate;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Request to create user.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserRequest {

    @ApiModelProperty(notes = "Username", required = true)
    @NotBlank
    @JsonProperty(value = "username")
    private String username;

    @ApiModelProperty(notes = "Password", required = true)
    @NotBlank
    @JsonProperty(value = "password")
    private String password;

    @ApiModelProperty(notes = "First name", required = true)
    @NotBlank
    @JsonProperty(value = "firstName")
    private String firstName;

    @ApiModelProperty(notes = "Last name", required = true)
    @NotBlank
    @JsonProperty(value = "lastName")
    private String lastName;

    @ApiModelProperty(notes = "Birth Date", required = true)
    //@NotNull
    @JsonProperty(value = "birthDate")
    private LocalDate birthDate;

    @ApiModelProperty(notes = "Gender", required = true)
    @NotNull
    @JsonProperty(value = "gender")
    @Size(min = 1, max = 1)
    private String gender;

    @ApiModelProperty(notes = "Phone Number", required = true)
    @NotBlank
    @Size(max = 10)
    @JsonProperty(value = "phoneNumber")
    private String phoneNumber;

    @ApiModelProperty(notes = "Document Number", required = true)
    @NotBlank
    @Size(max = 10)
    @JsonProperty(value = "documentNumber")
    private String documentNumber;

    @ApiModelProperty(notes = "User roles", required = true)
    @NotNull
    @JsonProperty(value = "userRoles")
    private List<String> userRoles;

    public CreateUserRequest() {
        super();
    }

    private CreateUserRequest(Builder builder) {
        username = builder.username;
        password = builder.password;
        firstName = builder.firstName;
        lastName = builder.lastName;
        birthDate = builder.birthDate;
        gender = builder.gender;
        phoneNumber = builder.phoneNumber;
        documentNumber = builder.documentNumber;
        userRoles = builder.userRoles;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public List<String> getUserRoles() {
        return userRoles;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private LocalDate birthDate;
        private String gender;
        private String phoneNumber;
        private String documentNumber;
        private List<String> userRoles;

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

        public Builder userRoles(List<String> userRoles) {
            this.userRoles = userRoles;
            return this;
        }

        public CreateUserRequest build() {
            return new CreateUserRequest(this);
        }

    }

}
