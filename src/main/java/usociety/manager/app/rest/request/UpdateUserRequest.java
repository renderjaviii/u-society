package usociety.manager.app.rest.request;

import java.util.Set;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.util.BaseObject;

@ApiModel(value = "Update user request.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateUserRequest extends BaseObject {

    @NotEmpty
    @JsonProperty
    private String name;

    @JsonProperty
    private String photo;

    //@Size(min = 1)
    @JsonProperty
    private Set<Long> categoryList;

    public UpdateUserRequest() {
        super();
    }

    public UpdateUserRequest(String name, Set<Long> categoryList) {
        this.name = name;
        this.categoryList = categoryList;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public Set<Long> getCategoryList() {
        return categoryList;
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
