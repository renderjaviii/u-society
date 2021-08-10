package usociety.manager.domain.service.post.dto;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import usociety.manager.app.util.BaseObject;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SurveyOption extends BaseObject {

    @JsonProperty
    private Integer id;

    @Schema(description = "Votes amount")
    @JsonProperty
    private Integer amount;

    @Schema(description = "Choice", example = "First choice")
    @NotEmpty
    @JsonProperty
    private String value;

    public SurveyOption() {
        super();
    }

    private SurveyOption(Builder builder) {
        id = builder.id;
        amount = builder.amount;
        value = builder.value;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
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

        private Integer id;
        private Integer amount;
        private String value;

        private Builder() {
            super();
        }

        public Builder id(Integer id) {
            this.id = id;
            return this;
        }

        public Builder amount(Integer amount) {
            this.amount = amount;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public SurveyOption build() {
            return new SurveyOption(this);
        }

    }

}
