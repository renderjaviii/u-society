package usociety.manager.domain.model;

import java.time.LocalDateTime;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("TEXT")
public class TextMessage extends Message {

    private TextMessage(Builder builder) {
        setId(builder.id);
        setContent(builder.content);
        setCreationDate(builder.creationDate);
        setGroup(builder.group);
        setUserId(builder.userId);
    }

    public TextMessage() {
        super();
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

    @Override
    public String toString() {
        return super.toString();
    }

    public static final class Builder {

        private Long id;
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

        public TextMessage build() {
            return new TextMessage(this);
        }

    }

}
