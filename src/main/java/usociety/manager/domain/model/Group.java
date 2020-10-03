package usociety.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "`group`")
public class Group {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "photo")
    private String photo;

    @Column(name = "objectives")
    private String objectives;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    public Group() {
        super();
    }

    private Group(Builder builder) {
        id = builder.id;
        name = builder.name;
        description = builder.description;
        photo = builder.photo;
        objectives = builder.objectives;
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

    public String getObjectives() {
        return objectives;
    }

    public Category getCategory() {
        return category;
    }

    public static final class Builder {

        private Long id;
        private String name;
        private String description;
        private String photo;
        private String objectives;
        private Category category;

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

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder photo(String photo) {
            this.photo = photo;
            return this;
        }

        public Builder objectives(String objectives) {
            this.objectives = objectives;
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
