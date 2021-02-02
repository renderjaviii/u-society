package usociety.manager.app.api;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.util.BaseObject;
import usociety.manager.domain.enums.MessageTypeEnum;

@ApiModel("Message")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MessageApi extends BaseObject {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String content;

    @JsonProperty
    private String image;

    @NotNull
    @JsonProperty
    private MessageTypeEnum type;

    @JsonProperty
    private LocalDateTime creationDate;

    @JsonProperty
    private UserApi user;

    @NotNull
    @JsonProperty
    private GroupApi group;

    public MessageApi() {
        super();
    }

    private MessageApi(Builder builder) {
        id = builder.id;
        content = builder.content;
        type = builder.type;
        creationDate = builder.creationDate;
        user = builder.user;
        group = builder.group;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageTypeEnum getType() {
        return type;
    }

    public void setType(MessageTypeEnum type) {
        this.type = type;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public UserApi getUser() {
        return user;
    }

    public GroupApi getGroup() {
        return group;
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

        private Long id;
        private String content;
        private MessageTypeEnum type;
        private LocalDateTime creationDate;
        private GroupApi group;
        private UserApi user;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder type(MessageTypeEnum type) {
            this.type = type;
            return this;
        }

        public Builder creationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder group(GroupApi group) {
            this.group = group;
            return this;
        }

        public Builder user(UserApi user) {
            this.user = user;
            return this;
        }

        public MessageApi build() {
            return new MessageApi(this);
        }

    }

}
