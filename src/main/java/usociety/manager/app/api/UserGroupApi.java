package usociety.manager.app.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel("User group information.")
public class UserGroupApi {

    @JsonProperty
    private Integer status;
    @JsonProperty
    private String role;
    @JsonProperty
    private Long groupId;
    @JsonProperty
    private Long userId;

    public UserGroupApi() {
        super();
    }

    private UserGroupApi(Builder builder) {
        status = builder.status;
        role = builder.role;
        groupId = builder.groupId;
        userId = builder.userId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getUserId() {
        return userId;
    }

    public static final class Builder {

        private int status;
        private String role;
        private Long groupId;
        private Long userId;

        private Builder() {
            super();
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder groupId(Long groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public UserGroupApi build() {
            return new UserGroupApi(this);
        }

    }

}
