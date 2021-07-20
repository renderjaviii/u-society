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
@Table(name = "user_groups", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "group_id" }))
public class UserGroup extends BaseObject {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "status", length = 10, nullable = false)
    private String status;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    public UserGroup() {
        super();
    }

    private UserGroup(Builder builder) {
        id = builder.id;
        status = builder.status;
        role = builder.role;
        isAdmin = builder.isAdmin;
        group = builder.group;
        userId = builder.userId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public Group getGroup() {
        return group;
    }

    public Long getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof UserGroup)) {
            return false;
        }
        return Objects.equals(((UserGroup) obj).id, id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static final class Builder {

        private Long id;
        private String status;
        private String role;
        private Boolean isAdmin;
        private Group group;
        private Long userId;

        private Builder() {
            super();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public Builder isAdmin(boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }

        public Builder group(Group group) {
            this.group = group;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public UserGroup build() {
            return new UserGroup(this);
        }

    }

}
