package usociety.manager.app.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;

@ApiModel("Group")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GroupApi {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private String photo;

    @JsonProperty
    private List<String> objectives;

    @JsonProperty
    private List<String> rules;

    @JsonProperty
    private CategoryApi category;

    public GroupApi() {
        super();
    }

    private GroupApi(Builder builder) {
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setObjectives(List<String> objectives) {
        this.objectives = objectives;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }

    public void setCategory(CategoryApi category) {
        this.category = category;
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

        public GroupApi build() {
            return new GroupApi(this);
        }

    }

}