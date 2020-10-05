package usociety.manager.app.api;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.domain.enums.MessageTypeEnum;

@ApiModel("Message")
public class MessageApi {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String content;

    @NotNull
    @JsonProperty
    private MessageTypeEnum type;

    @JsonProperty
    private LocalDate creationDate;

    @NotNull
    @JsonProperty
    private Long userId;

    @NotNull
    @JsonProperty
    private Long groupId;

    public MessageApi() {
        super();
    }

    private MessageApi(Builder builder) {
        id = builder.id;
        content = builder.content;
        type = builder.type;
        creationDate = builder.creationDate;
        userId = builder.userId;
        groupId = builder.groupId;
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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public static final class Builder {

        private Long id;
        private String content;
        private MessageTypeEnum type;
        private LocalDate creationDate;
        private Long userId;
        private Long groupId;

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

        public Builder creationDate(LocalDate creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder groupId(Long groupId) {
            this.groupId = groupId;
            return this;
        }

        public MessageApi build() {
            return new MessageApi(this);
        }

    }

}
