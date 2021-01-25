package usociety.manager.app.rest.request;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.util.BaseObject;
import usociety.manager.app.util.validator.PasswordCreationConstraint;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("Request to change password.")
public class ChangePasswordRequest extends BaseObject {

    @NotEmpty
    @JsonProperty
    private String oldPassword;

    @PasswordCreationConstraint
    @JsonProperty
    private String newPassword;

    public ChangePasswordRequest() {
        super();
    }

    private ChangePasswordRequest(Builder builder) {
        oldPassword = builder.oldPassword;
        newPassword = builder.newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private String oldPassword;
        private String newPassword;

        private Builder() {
            super();
        }

        public Builder oldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
            return this;
        }

        public Builder newPassword(String newPassword) {
            this.newPassword = newPassword;
            return this;
        }

        public ChangePasswordRequest build() {
            return new ChangePasswordRequest(this);
        }

    }

}

