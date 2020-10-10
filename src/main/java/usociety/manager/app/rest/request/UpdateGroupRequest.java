package usociety.manager.app.rest.request;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import usociety.manager.app.api.CategoryApi;

@ApiModel(value = "Request to update group.")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateGroupRequest {

    @NotNull
    @JsonProperty
    private Long id;

    @NotNull
    @JsonProperty
    private String name;

    @NotNull
    @JsonProperty
    private String description;

    @JsonProperty
    private String photo;

    @JsonProperty
    private List<String> objectives;

    @JsonProperty
    private List<String> rules;

    @NotNull
    @JsonProperty
    private CategoryApi category;

    public UpdateGroupRequest() {
        super();
    }

    private UpdateGroupRequest(Builder builder) {
        id = builder.id;
        name = builder.name;
        description = builder.description;
        photo = builder.photo;
        objectives = builder.objectives;
        rules = builder.rules;
        category = builder.category;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPhoto() {
        return photo;
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

    public static final class Builder {

        private Long id;
        private String name;
        private String description;
        private String photo;
        private List<String> objectives;
        private List<String> rules;
        private CategoryApi category;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
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

        public UpdateGroupRequest build() {
            return new UpdateGroupRequest(this);
        }

    }

}
