package usociety.manager.app.api;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import usociety.manager.app.util.BaseObject;

@ApiModel("Comment")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CommentApi extends BaseObject {

    @ApiModelProperty(notes = "User")
    @JsonProperty
    private UserApi user;

    @NotEmpty
    @ApiModelProperty(notes = "Value")
    @JsonProperty
    private String value;

    @ApiModelProperty(notes = "Creation date")
    @JsonProperty
    private LocalDateTime creationDate;

    public CommentApi() {
        super();
    }

    private CommentApi(Builder builder) {
        user = builder.user;
        value = builder.value;
        creationDate = builder.creationDate;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public UserApi getUser() {
        return user;
    }

    public String getValue() {
        return value;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
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

        private UserApi user;
        private String value;
        private LocalDateTime creationDate;

        private Builder() {
        }

        public Builder user(UserApi user) {
            this.user = user;
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