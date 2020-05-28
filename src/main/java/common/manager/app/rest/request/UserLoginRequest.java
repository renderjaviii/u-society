package common.manager.app.rest.request;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Request to user login.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginRequest {

    @NotNull
    @JsonProperty(value = "username")
    private String username;

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

    public static final class Builder {

        private @NotNull String username;
        private @NotNull String password;

        private Builder() {
            super();
        }

        public Builder username(@NotNull String username) {
            this.username = username;
            return this;
        }

        public Builder password(@NotNull String password) {
            this.password = password;
            return this;
        }

        public UserLoginRequest build() {
            return new UserLoginRequest(this);
        }

    }

}
