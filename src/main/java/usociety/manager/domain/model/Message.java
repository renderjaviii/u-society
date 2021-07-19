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
@Table(name = "messages")
public class Message extends BaseObject {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type", length = 1, nullable = false)
    private Integer type;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "creation_date", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime creationDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id", referencedColumnName = "id")
    private Group group;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    protected Message() {
        super();
    }

    private Message(Builder builder) {
        setId(builder.id);
        type = builder.type;
        setContent(builder.content);
        setCreationDate(builder.creationDate);
        setGroup(builder.group);
        setUserId(builder.userId);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Message)) {
            return false;
        }
        return Objects.equals(((Message) obj).id, id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static final class Builder {

        private Long id;
        private Integer type;
        private String content;
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

        public Builder type(Integer type) {
            this.type = type;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
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
