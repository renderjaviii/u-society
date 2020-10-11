package usociety.manager.app.rest.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.api.CategoryApi;

@ApiModel(value = "Update user request.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserRequest {

    @JsonProperty
    private String name;

    @JsonProperty
    private List<CategoryApi> categoryList;

    public UpdateUserRequest() {
        super();
    }

    public UpdateUserRequest(String name, List<CategoryApi> categoryList) {
        this.name = name;
        this.categoryList = categoryList;
    }

    public String getName() {
        return name;
    }

    public List<CategoryApi> getCategoryList() {
        return categoryList;
    }

}
