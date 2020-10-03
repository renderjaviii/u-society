package usociety.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "creation_date", nullable = false, columnDefinition = "DATE")
    private LocalDate creationDate;

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

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public static final class Builder {

        private Long id;
        private Post post;
        private Long userId;
        private String value;
        private LocalDate creationDate;

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

        public Builder creationDate(LocalDate creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public Comment build() {
            return new Comment(this);
        }

    }

}
