package usociety.manager.app.rest.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.app.util.BaseObject;
import usociety.manager.domain.enums.UserGroupStatusEnum;

@ApiModel(value = "Get group response.")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetGroupResponse extends BaseObject {

    @JsonProperty
    private GroupApi group;

    @JsonProperty
    private List<UserApi> activeMembers;

    @JsonProperty
    private List<UserApi> pendingMembers;

    @JsonProperty
    private UserGroupStatusEnum membershipStatus;

    @JsonProperty
    private boolean isAdmin;

    public GetGroupResponse() {
        super();
    }

    private GetGroupResponse(Builder builder) {
        group = builder.group;
        activeMembers = builder.activeMembers;
        pendingMembers = builder.pendingMembers;
        membershipStatus = builder.membershipStatus;
        isAdmin = builder.isAdmin;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public GroupApi getGroup() {
        return group;
    }

    public List<UserApi> getActiveMembers() {
        return activeMembers;
    }

    public List<UserApi> getPendingMembers() {
        return pendingMembers;
    }

    public UserGroupStatusEnum getMembershipStatus() {
        return membershipStatus;
    }

    public boolean isAdmin() {
        return isAdmin;
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

        private GroupApi group;
        private List<UserApi> activeMembers;
        private List<UserApi> pendingMembers;
        private UserGroupStatusEnum membershipStatus;
        private boolean isAdmin;

        private Builder() {
            super();
        }

        public Builder group(GroupApi group) {
            this.group = group;
            return this;
        }

        public Builder activeMembers(List<UserApi> activeMembers) {
            this.activeMembers = activeMembers;
            return this;
        }

        public Builder pendingMembers(List<UserApi> pendingMembers) {
            this.pendingMembers = pendingMembers;
            return this;
        }

        public Builder membershipStatus(UserGroupStatusEnum membershipStatus) {
            this.membershipStatus = membershipStatus;
            return this;
        }

        public Builder isAdmin(boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }

        public GetGroupResponse build() {
            return new GetGroupResponse(this);
        }

    }

}
