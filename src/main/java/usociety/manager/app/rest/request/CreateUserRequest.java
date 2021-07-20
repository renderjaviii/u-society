package usociety.manager.app.rest.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import usociety.manager.app.util.BaseObject;

@ApiModel(value = "Request to create user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserRequest extends BaseObject {

    @NotBlank
    @ApiModelProperty(notes = "Name", required = true)
    @JsonProperty(value = "name")
    private String name;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z\\d_]+")
    @ApiModelProperty(notes = "Username", required = true)
    @JsonProperty(value = "username")
    private String username;

    @NotBlank
    @Email
    @ApiModelProperty(notes = "Email", required = true)
    @JsonProperty(value = "email")
    private String email;

    @ApiModelProperty(notes = "Photo")
    @JsonProperty(value = "photo")
    private String photo;

    @NotBlank
    @ApiModelProperty(notes = "Password", required = true)
    @JsonProperty(value = "password")
    private String password;

    @ApiModelProperty(notes = "Otp code", required = true)
    @JsonProperty(value = "otpCode")
    private String otpCode;

    public CreateUserRequest() {
        super();
    }

    private CreateUserRequest(Builder builder) {
        name = builder.name;
        username = builder.username;
        email = builder.email;
        photo = builder.photo;
        password = builder.password;
        otpCode = builder.otpCode;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    public String getPassword() {
        return password;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static final class Builder {

        private String name;
        private String username;
        private String email;
        private String photo;
        private String password;
        private String otpCode;

        private Builder() {
            super();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder photo(String photo) {
            this.photo = photo;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder otpCode(String otpCode) {
            this.otpCode = otpCode;
            return this;
        }

        public CreateUserRequest build() {
            return new CreateUserRequest(this);
        }

    }

}
