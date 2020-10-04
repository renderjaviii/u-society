package usociety.manager.app.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel("React")
public class ReactApi {

    @JsonProperty
    private Long id;

    @JsonProperty
    private Long userId;

    @JsonProperty
    private int value;

    public ReactApi() {
        super();
    }

    private ReactApi(Builder builder) {
        id = builder.id;
        userId = builder.userId;
        value = builder.value;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public int getValue() {
        return value;
    }

    public static Builder newBuilder() {
        return new Builder();

    }

    public static final class Builder {

        private Long id;
        private Long userId;
        private int value;

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

        public Builder value(int value) {
            this.value = value;
            return this;
        }

        public ReactApi build() {
            return new ReactApi(this);
        }

    }

}
