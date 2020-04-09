package common.manager.app.api;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Error Api.")
public class ErrorApi {

    @JsonProperty
    private String description;
    @JsonProperty
    private String statusCode;

    public ErrorApi() {
        super();
    }

    public ErrorApi(String description, String statusCode) {
        this.description = description;
        this.statusCode = statusCode;
    }

    private ErrorApi(Builder builder) {
        description = builder.description;
        statusCode = builder.statusCode;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object o) {
        return reflectionEquals(this, o);
    }

    @Override
    public String toString() {
        return reflectionToString(this);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private String description;
        private String statusCode;

        private Builder() {
            super();
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder statusCode(String statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public ErrorApi build() {
            return new ErrorApi(this);
        }

    }

}