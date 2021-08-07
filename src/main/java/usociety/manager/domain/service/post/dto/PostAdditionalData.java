package usociety.manager.domain.service.post.dto;

import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.util.BaseObject;
import usociety.manager.domain.enums.PostTypeEnum;

@ApiModel("Post additional data based on the type.")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PostAdditionalData extends BaseObject {

    @JsonProperty
    private PostTypeEnum type;

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

    public PostTypeEnum getType() {
        return type;
    }

    public void setType(PostTypeEnum type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<SurveyOption> getOptions() {
        return options;
    }

    public void setOptions(List<SurveyOption> options) {
        this.options = options;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static final class Builder {

        private PostTypeEnum type;
        private String value;
        private List<SurveyOption> options;

        private Builder() {
        }

        public Builder type(PostTypeEnum type) {
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
