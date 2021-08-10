package usociety.manager.app.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import usociety.manager.app.util.BaseObject;
import usociety.manager.domain.enums.ReactTypeEnum;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ReactApi extends BaseObject {

    @JsonProperty
    private Integer amount;

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

    public void setAmount(Integer amount) {
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

        private Integer amount;
        private ReactTypeEnum value;

        private Builder() {
            super();
        }

        public Builder amount(Integer amount) {
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
