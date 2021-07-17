package usociety.manager.app.rest.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import usociety.manager.app.util.BaseObject;

@ApiModel(value = "Create user response.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserResponse extends BaseObject {

    @ApiModelProperty(notes = "OTP expiration time [ms]")
    @JsonProperty
    private LocalDateTime expiresAt;

    public CreateUserResponse() {
        super();
    }

    public CreateUserResponse(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
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
