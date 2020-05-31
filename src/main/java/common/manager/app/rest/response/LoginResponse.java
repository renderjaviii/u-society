package common.manager.app.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import common.manager.app.api.TokenApi;
import common.manager.app.api.UserApi;
import common.manager.app.util.BaseObject;
import io.swagger.annotations.ApiModel;

@ApiModel("Login response info.")
public class LoginResponse extends BaseObject {

    @JsonProperty(value = "user")
    private UserApi user;
    @JsonProperty(value = "token")
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
