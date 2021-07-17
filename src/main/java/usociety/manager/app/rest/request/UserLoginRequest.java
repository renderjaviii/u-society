package usociety.manager.app.rest.request;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.util.BaseObject;

@ApiModel(value = "Request to user login.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginRequest extends BaseObject {

    @NotNull
    @JsonProperty(value = "username")
    private String username;

    //TODO: Hide this data on logs
    @NotNull
    @JsonProperty(value = "password")
    private String password;

    public UserLoginRequest() {
        super();
    }

    private UserLoginRequest(Builder builder) {
        username = builder.username;
        password = builder.password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static Builder newBuilder() {
        return new Builder();
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

        private String username;
        private String password;

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

        public UserLoginRequest build() {
            return new UserLoginRequest(this);
        }

    }

}
