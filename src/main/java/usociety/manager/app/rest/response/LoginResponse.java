package usociety.manager.app.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import usociety.manager.app.api.TokenApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.util.BaseObject;

public class LoginResponse extends BaseObject {

    @JsonProperty
    private UserApi user;

    @Schema(description = "JWT")
    @JsonProperty
    private TokenApi token;

    public LoginResponse() {
        super();
    }

    public LoginResponse(UserApi user, TokenApi token) {
        this.user = user;
        this.token = token;
    }

    public UserApi getUser() {
        return user;
    }

    public TokenApi getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
