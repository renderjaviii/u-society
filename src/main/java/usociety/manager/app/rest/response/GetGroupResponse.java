package usociety.manager.app.rest.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserApi;
import usociety.manager.domain.enums.UserGroupStatusEnum;

@ApiModel(value = "Get group response.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GetGroupResponse {

    @JsonProperty
    private GroupApi group;

    @JsonProperty
    private List<UserApi> activeMembers;

    @JsonProperty
    private List<UserApi> pendingMembers;

    @JsonProperty
    private UserGroupStatusEnum membershipStatus;

    public GetGroupResponse() {
        super();
    }

    private GetGroupResponse(Builder builder) {
        group = builder.groupApi;
        activeMembers = builder.activeMembers;
        pendingMembers = builder.pendingMembers;
        membershipStatus = builder.membershipStatus;
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

    public void setMembershipStatus(UserGroupStatusEnum membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private GroupApi groupApi;
        private List<UserApi> activeMembers;
        private List<UserApi> pendingMembers;
        private UserGroupStatusEnum membershipStatus;

        private Builder() {
        }

        public Builder groupApi(GroupApi groupApi) {
            this.groupApi = groupApi;
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

        public GetGroupResponse build() {
            return new GetGroupResponse(this);
        }

    }

}
