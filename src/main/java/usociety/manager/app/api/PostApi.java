package usociety.manager.app.api;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.util.validator.PostCreationConstraint;
import usociety.manager.domain.service.post.dto.PostAdditionalData;

@ApiModel("Post")
@PostCreationConstraint
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PostApi {

    @JsonProperty
    private Long id;

    @JsonProperty
    private LocalDateTime expirationDate;

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

    public PostApi() {
        super();
    }

    private PostApi(Builder builder) {
        id = builder.id;
        expirationDate = builder.expirationDate;
        isPublic = builder.isPublic;
        content = builder.content;
        groupId = builder.groupId;
        reacts = builder.reacts;
        comments = builder.comments;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public PostAdditionalData getContent() {
        return content;
    }

    public void setContent(PostAdditionalData content) {
        this.content = content;
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

    public void setReacts(List<ReactApi> reacts) {
        this.reacts = reacts;
    }

    public void setComments(List<CommentApi> comments) {
        this.comments = comments;
    }

    public static final class Builder {

        private Long id;
        private LocalDateTime expirationDate;
        private boolean isPublic;
        private @Valid @NotNull PostAdditionalData content;
        private @NotNull Long groupId;
        private List<ReactApi> reacts;
        private List<CommentApi> comments;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder expirationDate(LocalDateTime expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder content(@Valid @NotNull PostAdditionalData content) {
            this.content = content;
            return this;
        }

        public Builder groupId(@NotNull Long groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder reacts(List<ReactApi> reacts) {
            this.reacts = reacts;
            return this;
        }

        public Builder comments(List<CommentApi> comments) {
            this.comments = comments;
            return this;
        }

        public PostApi build() {
            return new PostApi(this);
        }

    }

}
