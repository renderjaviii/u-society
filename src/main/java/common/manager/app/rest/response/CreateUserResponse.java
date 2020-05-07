package common.manager.app.rest.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Create User Response")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserResponse {

    @JsonProperty(value = "expiresAt")
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

}
