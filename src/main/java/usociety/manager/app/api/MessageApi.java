package usociety.manager.app.api;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import usociety.manager.app.util.BaseObject;
import usociety.manager.domain.enums.MessageTypeEnum;

@ApiModel("Message")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes( {
        @JsonSubTypes.Type(value = MessageApi.TextMessageApi.class, name = "TEXT"),
        @JsonSubTypes.Type(value = MessageApi.ImageMessageApi.class, name = "IMAGE")
})
public abstract class MessageApi extends BaseObject {

    @JsonProperty
    private Long id;

    @JsonProperty
    private LocalDateTime creationDate;

    @JsonProperty
    private UserApi user;

    @NotNull
    @JsonProperty
    private GroupApi group;

    protected MessageApi() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public UserApi getUser() {
        return user;
    }

    public void setUser(UserApi user) {
        this.user = user;
    }

    public GroupApi getGroup() {
        return group;
    }

    public void setGroup(GroupApi group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @ApiModel(value = "Image message")
    public static class ImageMessageApi extends MessageApi {

        @NotBlank
        @ApiModelProperty("Image description")
        @JsonProperty
        private String description;

        @NotEmpty
        @ApiModelProperty("Base 64 image")
        @JsonProperty
        private String content;

        public ImageMessageApi() {
            super();
        }

        private ImageMessageApi(Builder builder) {
            setId(builder.id);
            setCreationDate(builder.creationDate);
            setUser(builder.user);
            setGroup(builder.group);
            description = builder.description;
            content = builder.content;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public String getDescription() {
            return description;
        }

        public String getContent() {
            return content;
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
            private LocalDateTime creationDate;
            private UserApi user;
            private GroupApi group;
            private String description;
            private String content;

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

            public Builder description(String description) {
                this.description = description;
                return this;
            }

            public Builder content(String content) {
                this.content = content;
                return this;
            }

            public ImageMessageApi build() {
                return new ImageMessageApi(this);
            }

        }

    }

    @ApiModel(value = "Text message")
    public static class TextMessageApi extends MessageApi {

        @Pattern(regexp = "^[a-zA-Z\\d]+")
        @NotBlank
        @JsonProperty
        private String content;

        public TextMessageApi() {
            super();
        }

        private TextMessageApi(Builder builder) {
            setId(builder.id);
            setCreationDate(builder.creationDate);
            setUser(builder.user);
            setGroup(builder.group);
            content = builder.content;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public String getContent() {
            return content;
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
            private LocalDateTime creationDate;
            private UserApi user;
            private GroupApi group;
            private String content;

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

            public Builder content(String content) {
                this.content = content;
                return this;
            }

            public TextMessageApi build() {
                return new TextMessageApi(this);
            }

        }

    }

}
