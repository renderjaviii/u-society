package usociety.manager.app.api;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel("Comment")
public class CommentApi {

    @JsonProperty
    private Long id;

    @JsonProperty
    private Long userId;

    @JsonProperty
    private String value;

    @JsonProperty
    private LocalDateTime creationDate;

    public CommentApi() {
        super();
    }

    private CommentApi(Builder builder) {
        id = builder.id;
        userId = builder.userId;
        value = builder.value;
        creationDate = builder.creationDate;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getValue() {
        return value;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public static final class Builder {

        private Long id;
        private Long userId;
        private String value;
        private LocalDateTime creationDate;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder creationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public CommentApi build() {
            return new CommentApi(this);
        }

    }

}
