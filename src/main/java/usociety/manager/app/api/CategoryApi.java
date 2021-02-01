package usociety.manager.app.api;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.util.BaseObject;

@ApiModel("Category")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CategoryApi extends BaseObject {

    @NotNull
    @JsonProperty
    private Long id;

    @JsonProperty
    private String name;

    public CategoryApi() {
        super();
    }

    public CategoryApi(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
