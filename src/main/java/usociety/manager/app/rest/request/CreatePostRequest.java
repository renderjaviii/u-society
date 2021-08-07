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
import usociety.manager.app.util.BaseObject;
import usociety.manager.app.util.validator.PostCreationConstraint;
import usociety.manager.domain.service.post.dto.PostAdditionalData;

@ApiModel("Create post request.")
@PostCreationConstraint
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreatePostRequest extends BaseObject {

    @JsonProperty
    private LocalDateTime expirationDate;

    @JsonProperty
    private LocalDateTime creationDate;

    @JsonProperty
    private boolean isPublic;

    @JsonProperty
    private String image;

    @Valid
    @NotNull
    @JsonProperty
    private PostAdditionalData content;

    @JsonProperty
    private List<ReactApi> reacts;

    @JsonProperty
    private List<CommentApi> comments;

    @JsonProperty
    private String description;

    public CreatePostRequest() {
        super();
    }

    private CreatePostRequest(Builder builder) {
        expirationDate = builder.expirationDate;
        creationDate = builder.creationDate;
        isPublic = builder.isPublic;
        image = builder.image;
        content = builder.content;
        reacts = builder.reacts;
        comments = builder.comments;
        description = builder.description;
    }

    public static Builder newBuilder() {
        return new Builder();
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

    public List<ReactApi> getReacts() {
        return reacts;
    }

    public List<CommentApi> getComments() {
        return comments;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
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

        private LocalDateTime expirationDate;
        private LocalDateTime creationDate;
        private boolean isPublic;
        private String image;
        private PostAdditionalData content;
        private List<ReactApi> reacts;
        private List<CommentApi> comments;
        private String description;

        private Builder() {
        }

        public Builder expirationDate(LocalDateTime expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public Builder creationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder image(String image) {
            this.image = image;
            return this;
        }

        public Builder content(PostAdditionalData content) {
            this.content = content;
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

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public CreatePostRequest build() {
            return new CreatePostRequest(this);
        }

    }

}
