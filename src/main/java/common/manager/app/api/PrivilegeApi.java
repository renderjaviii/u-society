package common.manager.app.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("Privilege Api")
public class PrivilegeApi {

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    public PrivilegeApi() {
        super();
    }

    public PrivilegeApi(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
