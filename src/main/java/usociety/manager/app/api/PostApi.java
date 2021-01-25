package usociety.manager.app.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.util.BaseObject;
import usociety.manager.domain.enums.ReactTypeEnum;
import usociety.manager.domain.service.post.dto.PostAdditionalData;

@ApiModel("Post")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(value = "public")
public class PostApi extends BaseObject {

    @JsonProperty
    private Long id;

    @JsonProperty
    private LocalDateTime expirationDate;

    @JsonProperty
    private LocalDateTime creationDate;

    @JsonProperty
    private boolean isPublic;

    @JsonProperty
    private PostAdditionalData content;

    @JsonProperty
    private Map<ReactTypeEnum, Integer> reacts;

    @JsonProperty
    private List<CommentApi> comments;

    @JsonProperty
    private String description;

    @JsonProperty
    private GroupApi group;

    @JsonProperty
    private Integer selectedOptionId;

    @JsonProperty
    private ReactTypeEnum selectedReaction;

    public PostApi() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private PostApi(Builder builder) {
        id = builder.id;
        expirationDate = builder.expirationDate;
        creationDate = builder.creationDate;
        isPublic = builder.isPublic;
        content = builder.content;
        description = builder.description;
        reacts = builder.reacts;
        comments = builder.comments;
        group = builder.group;
        selectedOptionId = builder.selectedOptionId;
        selectedReaction = builder.selectedReaction;
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

    public Map<ReactTypeEnum, Integer> getReacts() {
        return reacts;
    }

    public List<CommentApi> getComments() {
        return comments;
    }

    public void setReacts(Map<ReactTypeEnum, Integer> reacts) {
        this.reacts = reacts;
    }

    public void setComments(List<CommentApi> comments) {
        this.comments = comments;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GroupApi getGroup() {
        return group;
    }

    public void setGroup(GroupApi group) {
        this.group = group;
    }

    public Integer getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Integer selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public ReactTypeEnum getSelectedReaction() {
        return selectedReaction;
    }

    public void setSelectedReaction(ReactTypeEnum selectedReaction) {
        this.selectedReaction = selectedReaction;
    }

    public static final class Builder {

        private Long id;
        private LocalDateTime expirationDate;
        private boolean isPublic;
        private PostAdditionalData content;
        private Map<ReactTypeEnum, Integer> reacts;
        private List<CommentApi> comments;
        private String description;
        private LocalDateTime creationDate;
        private GroupApi group;
        private Integer selectedOptionId;
        private ReactTypeEnum selectedReaction;

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

        public Builder creationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder content(PostAdditionalData content) {
            this.content = content;
            return this;
        }

        public Builder group(GroupApi group) {
            this.group = group;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder reacts(Map<ReactTypeEnum, Integer> reacts) {
            this.reacts = reacts;
            return this;
        }

        public Builder comments(List<CommentApi> comments) {
            this.comments = comments;
            return this;
        }

        public Builder selectedOptionId(Integer selectedOptionId) {
            this.selectedOptionId = selectedOptionId;
            return this;
        }

        public Builder selectedReaction(ReactTypeEnum selectedReaction) {
            this.selectedReaction = selectedReaction;
            return this;
        }

        public PostApi build() {
            return new PostApi(this);
        }

    }

}
