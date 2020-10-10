package usociety.manager.app.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel("Category")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CategoryApi {

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

}
