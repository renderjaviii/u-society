package usociety.manager.app.rest.request;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import usociety.manager.app.util.BaseObject;
import usociety.manager.app.util.validator.PasswordCreationConstraint;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public ChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
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

}

