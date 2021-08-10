package usociety.manager.app.rest.request;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import usociety.manager.app.util.BaseObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest extends BaseObject {

    @NotNull
    @JsonProperty(value = "username")
    private String username;

    //TODO: Hide this info from logs
    @NotNull
    @JsonProperty(value = "password")
    private String password;

    public LoginRequest() {
        super();
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
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
