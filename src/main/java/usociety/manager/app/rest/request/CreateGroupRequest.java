package usociety.manager.app.rest.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.api.CategoryApi;
import usociety.manager.app.util.BaseObject;

@ApiModel(value = "Create group request.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateGroupRequest extends BaseObject {

    @NotEmpty
    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private String photo;

    @Valid
    @NotNull
    @JsonProperty
    private CategoryApi category;

    @JsonProperty
    private List<String> objectives;

    @JsonProperty
    private List<String> rules;

    public CreateGroupRequest() {
        super();
    }

    private CreateGroupRequest(Builder builder) {
        name = builder.name;
        description = builder.description;
        objectives = builder.objectives;
        rules = builder.rules;
        category = builder.category;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getObjectives() {
        return objectives;
    }

    public List<String> getRules() {
        return rules;
    }

    public CategoryApi getCategory() {
        return category;
    }

    public String getPhoto() {
        return photo;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static final class Builder {

        private String name;
        private String description;
        private List<String> objectives;
        private List<String> rules;
        private CategoryApi category;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder objectives(List<String> objectives) {
            this.objectives = objectives;
            return this;
        }

        public Builder rules(List<String> rules) {
            this.rules = rules;
            return this;
        }

        public Builder category(CategoryApi category) {
            this.category = category;
            return this;
        }

        public CreateGroupRequest build() {
            return new CreateGroupRequest(this);
        }

    }

}