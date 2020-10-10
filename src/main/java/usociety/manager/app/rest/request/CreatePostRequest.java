package usociety.manager.app.rest.request;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.api.CommentApi;
import usociety.manager.app.api.ReactApi;
import usociety.manager.app.util.validator.PostCreationConstraint;
import usociety.manager.domain.service.post.dto.PostAdditionalData;

@ApiModel("Create post request.")
@PostCreationConstraint
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreatePostRequest {

    @JsonProperty
    private LocalDateTime expirationDate;

    @JsonProperty
    private LocalDateTime creationDate;

    @JsonProperty
    private boolean isPublic;

    @Valid
    @NotNull
    @JsonProperty
    private PostAdditionalData content;

    @NotNull
    @JsonProperty
    private Long groupId;

    @JsonProperty
    private List<ReactApi> reacts;

    @JsonProperty
    private List<CommentApi> comments;

    @JsonProperty
    private String description;

    public CreatePostRequest() {
        super();
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public PostAdditionalData getContent() {
        return content;
    }

    public Long getGroupId() {
        return groupId;
    }

    public List<ReactApi> getReacts() {
        return reacts;
    }

    public List<CommentApi> getComments() {
        return comments;
    }

    public String getDescription() {
        return description;
    }

}
