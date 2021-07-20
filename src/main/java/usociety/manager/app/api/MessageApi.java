package usociety.manager.app.api;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import usociety.manager.app.util.BaseObject;
import usociety.manager.domain.enums.MessageTypeEnum;

@ApiModel("Message")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MessageApi extends BaseObject {

    @ApiModelProperty("Id")
    @JsonProperty
    private Long id;

    @NotNull
    @ApiModelProperty("Type")
    @JsonProperty
    private MessageTypeEnum type;

    @NotEmpty
    @ApiModelProperty("Content")
    @JsonProperty
    private String content;

    @ApiModelProperty("Creation date")
    @JsonProperty
    private LocalDateTime creationDate;

    @ApiModelProperty("User")
    @JsonProperty
    private UserApi user;

    @NotNull
    @ApiModelProperty("Group")
    @JsonProperty
    private GroupApi group;

    protected MessageApi() {
        super();
    }

    private MessageApi(Builder builder) {
        id = builder.id;
        type = builder.type;
        content = builder.content;
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

    public MessageTypeEnum getType() {
        return type;
    }

    public String getContent() {
        return content;
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
        private MessageTypeEnum type;
        private String content;
        private LocalDateTime creationDate;
        private UserApi user;
        private GroupApi group;

        private Builder() {
            super();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder type(MessageTypeEnum type) {
            this.type = type;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder creationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder user(UserApi user) {
            this.user = user;
            return this;
        }

        public Builder group(GroupApi group) {
            this.group = group;
            return this;
        }

        public MessageApi build() {
            return new MessageApi(this);
        }

    }

}
