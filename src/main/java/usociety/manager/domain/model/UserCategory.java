package usociety.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "user_category", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "category_id" }))
public class UserCategory {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    public UserCategory() {
        super();
    }

    private UserCategory(Builder builder) {
        id = builder.id;
        userId = builder.userId;
        category = builder.category;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Category getCategory() {
        return category;
    }

    public static final class Builder {

        private Long id;
        private Long userId;
        private Category category;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public UserCategory build() {
            return new UserCategory(this);
        }

    }

}
