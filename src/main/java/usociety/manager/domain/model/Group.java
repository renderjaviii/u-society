package usociety.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import usociety.manager.app.util.BaseObject;
import usociety.manager.domain.util.StringListConverter;

@Entity
@Table(name = "`groups`")
public class Group extends BaseObject {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "slug", length = 100, nullable = false, unique = true)
    private String slug;

    @Column(name = "description")
    private String description;

    @Column(name = "photo")
    private String photo;

    @Column(name = "objectives")
    @Convert(converter = StringListConverter.class)
    private List<String> objectives;

    @Column(name = "rules")
    @Convert(converter = StringListConverter.class)
    private List<String> rules;

    @Column(name = "created_at", updatable = false, nullable = false, columnDefinition = "DATE")
    private LocalDate createdAt;

    @Column(name = "updated_at", columnDefinition = "DATE")
    private LocalDate updatedAt;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;

    public Group() {
        super();
    }

    private Group(Builder builder) {
        id = builder.id;
        name = builder.name;
        slug = builder.slug;
        description = builder.description;
        photo = builder.photo;
        objectives = builder.objectives;
        rules = builder.rules;
        createdAt = builder.createdAt;
        updatedAt = builder.updatedAt;
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

    public Category getCategory() {
        return category;
    }

    public String getSlug() {
        return slug;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Group)) {
            return false;
        }
        return Objects.equals(((Group) obj).id, id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static final class Builder {

        private Long id;
        private String name;
        private String description;
        private String photo;
        private List<String> objectives;
        private List<String> rules;
        private LocalDate createdAt;
        private LocalDate updatedAt;
        private Category category;
        private String slug;

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

        public Builder slug(String slug) {
            this.slug = slug;
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

        public Builder createdAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDate updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Group build() {
            return new Group(this);
        }

    }

}
