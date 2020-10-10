package usociety.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "creation_date", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime creationDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    public Message() {
        super();
    }

    private Message(Builder builder) {
        id = builder.id;
        content = builder.content;
        type = builder.type;
        creationDate = builder.creationDate;
        group = builder.group;
        userId = builder.userId;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Group getGroup() {
        return group;
    }

    public Long getUserId() {
        return userId;
    }

    public static final class Builder {

        private Long id;
        private String content;
        private int type;
        private LocalDateTime creationDate;
        private Group group;
        private Long userId;

        private Builder() {
            super();
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder creationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
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

        public Message build() {
            return new Message(this);
        }

    }

}
