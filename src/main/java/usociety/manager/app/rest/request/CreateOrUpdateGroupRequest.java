package usociety.manager.app.rest.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.api.CategoryApi;
import usociety.manager.app.util.BaseObject;
import usociety.manager.app.util.validator.AlphanumericConstraint;

@ApiModel(value = "Create or update group request.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrUpdateGroupRequest extends BaseObject {

    @NotEmpty
    @Size(max = 100)
    @AlphanumericConstraint
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

    @NotNull
    @Size(min = 1)
    @JsonProperty
    private List<String> objectives;

    @NotNull
    @Size(min = 1)
    @JsonProperty
    private List<String> rules;

    public CreateOrUpdateGroupRequest() {
        super();
    }

    private CreateOrUpdateGroupRequest(Builder builder) {
        name = builder.name;
        description = builder.description;
        photo = builder.photo;
        category = builder.category;
        objectives = builder.objectives;
        rules = builder.rules;
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
        private String photo;
        private List<String> objectives;
        private List<String> rules;
        private CategoryApi category;

        private Builder() {
            super();
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder photo(String photo) {
            this.photo = photo;
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

        public CreateOrUpdateGroupRequest build() {
            return new CreateOrUpdateGroupRequest(this);
        }

    }

}