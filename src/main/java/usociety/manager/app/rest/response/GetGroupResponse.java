package usociety.manager.app.rest.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.api.GroupApi;
import usociety.manager.app.api.UserApi;

@ApiModel(value = "Get Group Response")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetGroupResponse {

    @JsonProperty
    private GroupApi groupApi;

    @JsonProperty
    private List<UserApi> activeMembers;

    @JsonProperty
    private List<UserApi> pendingMembers;

    public GetGroupResponse() {
        super();
    }

    public GroupApi getGroupApi() {
        return groupApi;
    }

    public List<UserApi> getActiveMembers() {
        return activeMembers;
    }

    public List<UserApi> getPendingMembers() {
        return pendingMembers;
    }

    private GetGroupResponse(Builder builder) {
        groupApi = builder.groupApi;
        activeMembers = builder.activeMembers;
        pendingMembers = builder.pendingMembers;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private GroupApi groupApi;
        private List<UserApi> activeMembers;
        private List<UserApi> pendingMembers;

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

        public GetGroupResponse build() {
            return new GetGroupResponse(this);
        }

    }

}
