package usociety.manager.app.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import usociety.manager.app.util.BaseObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserApi extends BaseObject {

    @JsonProperty
    private Long id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String email;

    @JsonProperty
    private String username;

    @JsonProperty
    private String photo;

    @JsonProperty
    private LocalDate createdAt;

    @JsonProperty
    private LocalDateTime lastAccessAt;

    @JsonProperty
    private String role;

    @JsonProperty
    private List<CategoryApi> categoryList;

    public UserApi() {
        super();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastAccessAt() {
        return lastAccessAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<CategoryApi> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<CategoryApi> categoryList) {
        this.categoryList = categoryList;
    }

    private UserApi(Builder builder) {
        id = builder.id;
        name = builder.name;
        email = builder.email;
        username = builder.username;
        photo = builder.photo;
        createdAt = builder.createdAt;
        lastAccessAt = builder.lastAccessAt;
        role = builder.role;
        categoryList = builder.categoryList;
    }

    public static Builder newBuilder() {
        return new Builder();
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

        private Long id;
        private String name;
        private String email;
        private String username;
        private String photo;
        private LocalDate createdAt;
        private LocalDateTime lastAccessAt;
        private String role;
        private List<CategoryApi> categoryList;

        private Builder() {
            super();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder photo(String photo) {
            this.photo = photo;
            return this;
        }

        public Builder createdAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder lastAccessAt(LocalDateTime lastAccessAt) {
            this.lastAccessAt = lastAccessAt;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder categoryList(List<CategoryApi> categoryList) {
            this.categoryList = categoryList;
            return this;
        }

        public UserApi build() {
            return new UserApi(this);
        }

    }

}
