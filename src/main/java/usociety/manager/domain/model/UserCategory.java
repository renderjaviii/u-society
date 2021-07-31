package usociety.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import usociety.manager.app.util.BaseObject;

@Entity
@Table(name = "user_categories", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "category_id" }))
public class UserCategory extends BaseObject {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;

    public UserCategory() {
        super();
    }

    public UserCategory(Long userId, Category category) {
        this.userId = userId;
        this.category = category;
    }

    public UserCategory(Long id, Long userId, Category category) {
        this.id = id;
        this.userId = userId;
        this.category = category;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserCategory)) {
            return false;
        }
        return Objects.equals(((UserCategory) obj).id, id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
