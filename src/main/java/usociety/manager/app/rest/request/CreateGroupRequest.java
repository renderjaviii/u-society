package usociety.manager.app.rest.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Create Group Request")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateGroupRequest {

    @NotEmpty
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Long categoryId;
    @NotNull
    private String photo;

    private String[] objectives;
    private String[] rules;

    public CreateGroupRequest() {
        super();
    }

    private CreateGroupRequest(Builder builder) {
        name = builder.name;
        description = builder.description;
        photo = builder.photo;
        objectives = builder.objectives;
        rules = builder.rules;
        categoryId = builder.categoryId;
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

    public String getPhoto() {
        return photo;
    }

    public String[] getObjectives() {
        return objectives;
    }

    public String[] getRules() {
        return rules;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public static final class Builder {

        private String name;
        private String description;
        private String photo;
        private String[] objectives;
        private String[] rules;
        private Long categoryId;

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

        public Builder photo(String photo) {
            this.photo = photo;
            return this;
        }

        public Builder objectives(String[] objectives) {
            this.objectives = objectives;
            return this;
        }

        public Builder rules(String[] rules) {
            this.rules = rules;
            return this;
        }

        public Builder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public CreateGroupRequest build() {
            return new CreateGroupRequest(this);
        }

    }

}