package usociety.manager.app.rest.request;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

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

    @NotNull
    @JsonProperty
    private String photo;

    @JsonProperty
    private String[] objectives;

    @JsonProperty
    private String[] rules;

    @NotNull
    @JsonProperty
    private Long categoryId;

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
        categoryId = builder.categoryId;
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

        private Long id;
        private String name;
        private String description;
        private String photo;
        private String[] objectives;
        private String[] rules;
        private Long categoryId;

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

        public UpdateGroupRequest build() {
            return new UpdateGroupRequest(this);
        }

    }

}
