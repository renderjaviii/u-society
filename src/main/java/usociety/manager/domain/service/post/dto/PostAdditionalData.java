package usociety.manager.domain.service.post.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.api.SurveyOption;

@ApiModel("Post additional data based on the type.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PostAdditionalData {

    @JsonProperty
    private int type;

    @NotNull
    @JsonProperty
    private String value;

    @Valid
    @JsonProperty
    private List<SurveyOption> options;

    public PostAdditionalData() {
        super();
    }

    private PostAdditionalData(Builder builder) {
        type = builder.type;
        value = builder.value;
        options = builder.options;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public List<SurveyOption> getOptions() {
        return options;
    }

    public static final class Builder {

        private int type;
        private String value;
        private List<SurveyOption> options;

        private Builder() {
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder options(List<SurveyOption> options) {
            this.options = options;
            return this;
        }

        public PostAdditionalData build() {
            return new PostAdditionalData(this);
        }

    }

}
