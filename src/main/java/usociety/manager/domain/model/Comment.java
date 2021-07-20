package usociety.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import usociety.manager.app.util.BaseObject;

@Entity
@Table(name = "comments")
public class Comment extends BaseObject {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Post post;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "creation_date", nullable = false, updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime creationDate;

    public Comment() {
        super();
    }

    private Comment(Builder builder) {
        id = builder.id;
        post = builder.post;
        userId = builder.userId;
        value = builder.value;
        creationDate = builder.creationDate;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public Long getUserId() {
        return userId;
    }

    public String getValue() {
        return value;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Comment)) {
            return false;
        }
        return Objects.equals(((Comment) obj).id, id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static final class Builder {

        private Long id;
        private Post post;
        private Long userId;
        private String value;
        private LocalDateTime creationDate;

        private Builder() {
            super();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder post(Post post) {
            this.post = post;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder creationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Comment build() {
            return new Comment(this);
        }

    }

}
