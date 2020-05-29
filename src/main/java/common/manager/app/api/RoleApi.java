package common.manager.app.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("Role Api")
public class RoleApi {

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private List<PrivilegeApi> privileges;

    public RoleApi() {
        super();
    }

    private RoleApi(Builder builder) {
        name = builder.name;
        description = builder.description;
        privileges = builder.privileges;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<PrivilegeApi> getPrivileges() {
        return privileges;
    }

    public static final class Builder {

        private String name;
        private String description;
        private List<PrivilegeApi> privileges;

        private Builder() {
            super();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder privileges(List<PrivilegeApi> privileges) {
            this.privileges = privileges;
            return this;
        }

        public RoleApi build() {
            return new RoleApi(this);
        }

    }

}
