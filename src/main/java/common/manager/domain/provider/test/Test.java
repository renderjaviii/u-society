package common.manager.domain.provider.test;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Test {

    @JsonProperty
    public int code;
    @JsonProperty
    public String description;

    public Test() {
        super();
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}