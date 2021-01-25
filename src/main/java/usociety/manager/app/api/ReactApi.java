package usociety.manager.app.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.util.BaseObject;
import usociety.manager.domain.enums.ReactTypeEnum;

@ApiModel("React")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReactApi extends BaseObject {

    @JsonProperty
    private int amount;

    @JsonProperty
    private ReactTypeEnum value;

    public ReactApi() {
        super();
    }

    private ReactApi(Builder builder) {
        amount = builder.amount;
        value = builder.value;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ReactTypeEnum getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private int amount;
        private ReactTypeEnum value;

        private Builder() {
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public Builder value(ReactTypeEnum value) {
            this.value = value;
            return this;
        }

        public ReactApi build() {
            return new ReactApi(this);
        }

    }

}
