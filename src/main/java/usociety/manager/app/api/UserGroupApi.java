package usociety.manager.app.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.util.BaseObject;
import usociety.manager.domain.enums.UserGroupStatusEnum;

@ApiModel("User group information.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserGroupApi extends BaseObject {

    @JsonProperty
    private UserApi user;

    @JsonProperty
    private String role;

    @JsonProperty
    private UserGroupStatusEnum status;

    public UserGroupApi() {
        super();
    }

    private UserGroupApi(Builder builder) {
        status = builder.status;
        role = builder.role;
        user = builder.user;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public UserGroupStatusEnum getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }

    public UserApi getUser() {
        return user;
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

        private UserGroupStatusEnum status;
        private String role;
        private UserApi user;

        private Builder() {
            super();
        }

        public Builder status(UserGroupStatusEnum status) {
            this.status = status;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder user(UserApi user) {
            this.user = user;
            return this;
        }

        public UserGroupApi build() {
            return new UserGroupApi(this);
        }

    }

}
