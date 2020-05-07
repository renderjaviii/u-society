package common.manager.domain.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "document_number", unique = true, nullable = false)
    private String documentNumber;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "gender", length = 1)
    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATE")
    private LocalDate createdAt;

    @Column(name = "last_access_at", columnDefinition = "DATETIME")
    private LocalDateTime lastAccessAt;

    @Column(name = "account_locked", nullable = false, insertable = false, columnDefinition = "boolean default false")
    private boolean accountLocked;

    @Column(name = "email_verified", nullable = false, insertable = false, columnDefinition = "boolean default false")
    private boolean emailVerified;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    public User() {
        super();
    }

    public User(User user) {
        this.accountLocked = user.accountLocked;
        this.role = user.role;
    }

    private User(Builder builder) {
        username = builder.username;
        password = builder.password;
        firstName = builder.firstName;
        lastName = builder.lastName;
        documentNumber = builder.documentNumber;
        email = builder.email;
        gender = builder.gender;
        birthDate = builder.birthDate;
        phoneNumber = builder.phoneNumber;
        createdAt = builder.createdAt;
        lastAccessAt = builder.lastAccessAt;
        role = builder.role;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setLastAccessAt(LocalDateTime lastAccessAt) {
        this.lastAccessAt = lastAccessAt;
    }

    public LocalDateTime getLastAccessAt() {
        return lastAccessAt;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User that = (User) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static final class Builder {

        private String username;
        private String password;
        private String firstName;
        private String lastName;
        private String documentNumber;
        private String email;
        private String gender;
        private LocalDate birthDate;
        private String phoneNumber;
        private LocalDate createdAt;
        private LocalDateTime lastAccessAt;
        private Role role;

        private Builder() {
            super();
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder documentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder createdAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder lastAccessAt(LocalDateTime lastAccessAt) {
            this.lastAccessAt = lastAccessAt;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public User build() {
            return new User(this);
        }

    }

}